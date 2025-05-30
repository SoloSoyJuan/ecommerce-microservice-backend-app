import random
from locust import HttpUser, task, between
import os

class TestOrderService(HttpUser):
    
    wait_time = between(1, 3)

    host = os.getenv("ORDER_SERVICE_HOST", "http://localhost:8000")


    @task
    def get_orders(self):
        self.client.get("/order-service/api/orders")

    @task
    def save_order(self):

        orderId = random.randint(1000, 9999)

        self.client.post("/order-service/api/orders", 
                         json={
                            "orderId": orderId,
                            "orderDate": "11-11-2025__12:55:55:546693",
                            "orderDesc": "lorem ipsum dolor sit amet",
                            "orderFee": 1000,
                            "cart": {
                                "cartId": 1,
                                "userId": 1
                            }
                         })
    
    @task
    def get_order_by_id(self):

        orderId = random.randint(1000, 9999)

        self.client.get(f"/order-service/api/orders/{orderId}")

    @task
    def update_order(self):

        orderId = random.randint(1000, 9999)

        self.client.put(f"/order-service/api/orders", 
                        json={
                            "orderId": orderId,
                            "orderDate": "11-11-2025__12:55:55:546693",
                            "orderDesc": "lorem ipsum dolor sit amet UPDATE!!!!",
                            "orderFee": 1200,
                            "cart": {
                                "cartId": 2,
                                "userId": 2
                            }
                        })
        
    @task
    def delete_order(self):

        orderId = random.randint(1000, 9999)

        self.client.delete(f"/order-service/api/orders/{orderId}")