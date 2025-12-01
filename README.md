# Introduction to Calculation-of-products-on-Pinduoduo Project

This project is a dedicated data management and intelligent analysis tool for Pinduoduo merchants. It aims to help merchants efficiently manage order data, monitor business performance, and optimize decision-making through automated data processing and AI analysis capabilities. 

![619b2eb6d2aa237e593ea093c5af0743](https://github.com/user-attachments/assets/2653504e-3d90-49a5-bfb5-461aa35ddc80)

Below are its core features and highlights:


## Core Feature Highlights

### 1. Scheduled Task Automation: Order Transaction Pulling and Management
- **Scheduled Order Pulling**: Supports configuring scheduled tasks (based on set intervals) to automatically pull order transactions and details from Pinduoduo APIs. No manual intervention is required, ensuring data real-time nature.
- **Automatic Database Writing**: Pulled order data (including product ID, name, cost, price, sales volume, etc.) is automatically written into the database, forming structured data storage for subsequent analysis.


### 2. Real-time Profit Monitoring and Threshold Alerts
- **Dynamic Profit Monitoring**: Periodically calculates the total cost, total revenue, and profit of products in the database through scheduled tasks (automatically computed based on sales volume × price/cost).
- **Custom Threshold Alerts**: Allows setting profit thresholds (`profitThreshold`). When the monitored profit falls below the threshold, an alert mechanism is automatically triggered to promptly remind merchants of operational risks.

![1593348038fb88d9f7b6f769fa93a2c9](https://github.com/user-attachments/assets/1fe2fc50-e862-4772-ba10-893e6e152d39)


### 3. AI Intelligent Prediction and Streaming Output
- **Integration with DeepSeek Large Model**: Incorporates AI capabilities to conduct in-depth analysis and predictions based on historical sales data.
- **Support for Streaming Output**: Real-time returns analysis results through the streaming request interface (`/api/database/ai/sales-forecast`), enhancing user experience.
- **Multi-dimensional Predictive Analysis**: Includes historical trend identification, influencing factor analysis (such as seasonality, price fluctuations), future sales volume prediction, and generates visualized chart data to support inventory management and marketing strategy decisions.

<div style="text-align: center;">
  <img src="https://github.com/user-attachments/assets/0caa2d7c-1e36-4166-be23-0709c2d53f49" alt="43f1020f3eebdc2fbf73ba301161bcbf">
</div>


## Key Technical Implementations
- **Scheduled Task Scheduling**: Implements timed task execution based on a thread pool scheduler (`ThreadPoolTaskScheduler`), supporting task start/stop and status monitoring.
- **Data Processing Pipeline**: Automates the entire process from API pulling (`executeApiMetric`), data parsing to database writing (`batchInsert`).
- **Flexible Configuration**: Enables configuring task types (API/database), scheduling intervals, alert thresholds, etc., through the `Metric` model, adapting to different business scenarios.

<p align="center">
  <img src="https://github.com/user-attachments/assets/0caa2d7c-1e36-4166-be23-0709c2d53f49" alt="43f1020f3eebdc2fbf73ba301161bcbf">
</p>

This project provides Pinduoduo merchants with a full-link solution from data collection, storage, monitoring to intelligent analysis, effectively reducing manual operation costs and improving the accuracy of business decisions.
