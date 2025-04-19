from locust import HttpUser, task, between, SequentialTaskSet, constant_throughput
from faker import Faker
import random

fake = Faker("ru_RU")

class BuildingSystemUser(SequentialTaskSet):
    def on_start(self):
        self.login()

    def login(self):
        login_data = {
            "mhiPolicy": "44444444",
            "password": "Admin123!"
        }
        self.client.post("/api/auth/login", json=login_data)

    """ @task
    def create_building(self):
        building_data = {
            "buildingName": fake.company(),
            "address": fake.address()
        }
        response = self.client.post("/api/buildings", json=building_data)
        if response.status_code == 201:
            building_id = response.json().get("id")
            if building_id:
                self.update_building(building_id)
                self.delete_building(building_id)

    @task
    def get_all_buildings(self):
        self.client.get("/api/buildings")

    @task
    def get_building_by_id(self):
        building_id = random.randint(1, 100)
        self.client.get(f"/api/buildings/{building_id}")

    def update_building(self, building_id):
        update_data = {
            "id": building_id,
            "buildingName": fake.company(),
            "address": fake.address()
        }
        self.client.put(f"/api/buildings/{building_id}", json=update_data)

    def delete_building(self, building_id):
        self.client.delete(f"/api/buildings/{building_id}") """

    @task
    def get_all_medicines(self):
        self.client.get("/api/medicines")

    @task
    def add_medicines(self):
        medicine_data = {
            "medicineName": fake.city_name(),
            "manufacturer": fake.country(),
        }
        self.client.post("/api/medicines", json=medicine_data)

    @task
    def get_medicine_by_id(self):
        medicine_id = random.randint(1, 70)
        self.client.get(f"/api/medicines/{medicine_id}")

class WebsiteUser(HttpUser):
    wait_time = constant_throughput(2)

    tasks = [
        BuildingSystemUser
    ]
