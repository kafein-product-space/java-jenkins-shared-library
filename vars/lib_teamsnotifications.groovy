def notify(String status, String message, String credentialsId) {
  def colors = [
      'Success': 'Good', // green
      'Failure': 'Attention' // red
  ]

  if (!colors.containsKey(status)) {
      error "Invalid status: $status"
  }

  def color = colors[status]

  def title = "Jenkins Build Notification"

  withCredentials([string(credentialsId: "teams-webhook-url", variable: 'webhookUrl')]) {
      def payload = [
        "type": "message",
        "attachments": [
            [
              "contentType": "application/vnd.microsoft.card.adaptive",
              "content": [
                  "\$schema": "http://adaptivecards.io/schemas/adaptive-card.json",
                  "type": "AdaptiveCard",
                  "version": "1.3",
                  "body": [
                    [
                        "type": "TextBlock",
                        "size": "Medium",
                        "weight": "Bolder",
                        "text": "${title}"
                    ],
                    [
                        "type": "ColumnSet",
                        "columns": [
                          [
                              "type": "Column",
                              "items": [
                                [
                                    "type": "Image",
                                    "style": "Person",
                                    "url": "https://ftp-chi.osuosl.org/pub/jenkins/art/jenkins-logo/1024x1024/logo.png",
                                    "altText": "Jenkins",
                                    "size": "Medium"
                                ]
                              ],
                              "width": "auto"
                          ],
                          [
                              "type": "Column",
                              "items": [
                                [
                                    "type": "TextBlock",
                                    "weight": "Bolder",
                                    "text": "Jenkins Notification",
                                    "wrap": true
                                ]
                              ],
                              "width": "auto"
                          ]
                        ]
                    ],
                    [
                        "type": "TextBlock",
                        "weight": "Bolder",
                        "color": "${color}",
                        "text": "${status}",
                        "wrap": true
                    ],
                    [
                        "type": "TextBlock",
                        "text": "${message}",
                        "wrap": true
                    ]
                  ]
              ]
            ]
        ]
      ]

      def response = httpRequest(
        httpMode: 'POST',
        contentType: 'APPLICATION_JSON',
        requestBody: groovy.json.JsonOutput.toJson(payload),
        url: webhookUrl
      )

      echo "Response: ${response}"
  }
}