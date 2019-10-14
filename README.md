# firebase-chat
1) Create your Firebase project, download "google-services.json" file and add it in your project path:"ChatApp/app/"
![Screenshot from 2019-10-14 11-15-11](https://user-images.githubusercontent.com/39268487/66731730-6fc14700-ee76-11e9-9be1-fa3f7901f3c9.png)

2) Replace your project key instead of "https://chat-app-ab12cd34.firebaseio.com" (Dummy) to your new one(Valid one) where ever it is used in project.

3) Replace your Firebase Legacy key in com/example/chatapp/Chat.java class in sendNotification().getHeaders() method to get notification of messages.
![Screenshot from 2019-10-14 11-15-01](https://user-images.githubusercontent.com/39268487/66731729-6df78380-ee76-11e9-8dfd-afb8023a2030.png)

4) Change rule of Storage and database as below as of now I am allowing read and write permission for all,
![Screenshot from 2019-10-14 11-15-35](https://user-images.githubusercontent.com/39268487/66731734-718b0a80-ee76-11e9-886f-20895bc3059f.png)
![Screenshot from 2019-10-14 11-15-48](https://user-images.githubusercontent.com/39268487/66731736-73ed6480-ee76-11e9-9751-ae0720f6926a.png)

and BOOM your project is setup and ready to run
