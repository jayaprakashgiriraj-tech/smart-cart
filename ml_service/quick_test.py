import sys, os
sys.path.insert(0, r"c:\Users\admin\AndroidStudioProjects\SMARTCART\ml_service")
sys.path.insert(0, r"c:\Users\admin\AndroidStudioProjects\SMARTCART\ml_service\scripts")
import main

print("1. Calling health_check()...")
h = main.health_check()
print("Health:", h)

print("\n2. Calling search_products()...")
s_req = main.SearchRequest(query="Best gaming laptop under 60000 with 16GB RAM", user_id=10, page_size=3)
s_res = main.search_products(s_req)
print(f"Search results count: {len(s_res['results'])} | Top: {s_res['results'][0]['name']}")
