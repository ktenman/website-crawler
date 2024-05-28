package main

import (
    "encoding/json"
    "fmt"
    "log"
    "net/url"

    "github.com/gocolly/colly"
    "github.com/gocolly/colly/extensions"
    "github.com/streadway/amqp"
)

type WebsiteData struct {
    URL  string `json:"url"`
    Body string `json:"body"`
}

type CrawlerTask struct {
    Timestamp   string `json:"timestamp"`
    Originator  string `json:"originator"`
    WebsiteURL string `json:"websiteUrl"`
}

func main() {
    // RabbitMQ connection setup
    conn, err := amqp.Dial("amqp://admin:admin@rabbitmq:5672/")
    if err != nil {
        log.Fatalf("Failed to connect to RabbitMQ: %v", err)
    }
    defer conn.Close()

    ch, err := conn.Channel()
    if err != nil {
        log.Fatalf("Failed to open a channel: %v", err)
    }
    defer ch.Close()

    // Declare the "crawler_tasks" queue (producer)
    crawlerTasksQueue, err := ch.QueueDeclare(
        "crawler_tasks", // name
        false,           // durable
        false,           // delete when unused
        false,           // exclusive
        false,           // no-wait
        nil,             // arguments
    )
    if err != nil {
        log.Fatalf("Failed to declare the 'crawler_tasks' queue: %v", err)
    }

    // Declare the "websites" queue (consumer)
    websitesQueue, err := ch.QueueDeclare(
        "websites", // name
        false,      // durable
        false,      // delete when unused
        false,      // exclusive
        false,      // no-wait
        nil,        // arguments
    )
    if err != nil {
        log.Fatalf("Failed to declare the 'websites' queue: %v", err)
    }

    msgs, err := ch.Consume(
        crawlerTasksQueue.Name, // queue
        "",                     // consumer
        true,                   // auto-ack
        false,                  // exclusive
        false,                  // no-local
        false,                  // no-wait
        nil,                    // args
    )
    if err != nil {
        log.Fatalf("Failed to register a consumer: %v", err)
    }

    forever := make(chan bool)

    go func() {
        for msg := range msgs {
            var task CrawlerTask
            err := json.Unmarshal(msg.Body, &task)
            if err != nil {
                log.Printf("Failed to parse app task: %v", err)
                continue
            }

            log.Printf("Received a app task: %+v", task)

            // Colly setup
            c := colly.NewCollector(
                colly.Async(true),
                colly.MaxDepth(3),
                colly.MaxBodySize(10*1024*1024),
            )

            extensions.RandomUserAgent(c)
            extensions.Referer(c)

            c.Limit(&colly.LimitRule{
                DomainGlob:  "*",
                Parallelism: 100,
            })

            c.OnHTML("a[href]", func(e *colly.HTMLElement) {
                link := e.Attr("href")
                absoluteURL, err := url.Parse(e.Request.AbsoluteURL(link))
                if err != nil {
                    log.Printf("Failed to parse URL: %s, error: %v", link, err)
                    return
                }

                err = e.Request.Visit(absoluteURL.String())
                if err != nil {
                    log.Printf("Failed to visit URL: %s, error: %v", absoluteURL.String(), err)
                }
            })

            c.OnResponse(func(r *colly.Response) {
                fmt.Printf("Visited: %s\n", r.Request.URL)
                data := WebsiteData{
                    URL:  r.Request.URL.String(),
                    Body: string(r.Body),
                }
                jsonData, err := json.Marshal(data)
                if err != nil {
                    log.Printf("Failed to marshal data: %v", err)
                    return
                }
                err = ch.Publish(
                    "",                  // exchange
                    websitesQueue.Name, // routing key
                    false,               // mandatory
                    false,               // immediate
                    amqp.Publishing{
                        ContentType: "application/json",
                        Body:        jsonData,
                    })
                if err != nil {
                    log.Printf("Failed to publish message: %v", err)
                }
            })

            err = c.Visit(task.WebsiteURL)
            if err != nil {
                log.Printf("Failed to start crawling: %v", err)
            }

            c.Wait()
        }
    }()

    log.Printf("Waiting for app tasks. To exit press CTRL+C")
    <-forever
}
