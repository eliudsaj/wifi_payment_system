<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="theme-color" content="#fff"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Success Login</title>
    <link rel="stylesheet" href="style.css" />
    <link
      href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css"
      rel="stylesheet"
    />
  </head>
  <body class="bg-gray-100 h-screen">

    <div class="w-full md:w-1/3 mx-auto">
      <div
        class="flex justify-between text-green-500 px-7 py-4 text-xl mx-auto bg-white"
      >
        <a href="alogin.html"><span class="fa fa-home"></span></a>
        <a href="logout.html"><span class="fa fa-sign-out"></span></a>
      </div>

      <div class="text-center py-14">
        <p class="text-7xl"><span class="fa fa-check-circle-o text-green-500"></span></p>
        <p class="text-green-500 font-bold">Success! You are now connected.</p>
        <p>You can now access the internet!</p>
      </div>

      <p class="uppercase text-sm m-5 py-2 text-gray-400">Your Balances</p>

      <!-- Active Access Code -->
      <div
        class="bg-green-500 rounded-md flex justify-between items-center m-5 px-3 py-3 text-white"
      >
        <span class="text-sm"><span class="fa fa-envelope px-2"></span>Active Access Code</span>
        <span class="text-sm font-bold">${username}</span> <!-- Use EL to display the username -->
      </div>

      <!-- Bundle Expiration -->
      <div
        class="bg-green-500 rounded-md flex justify-between items-center m-5 px-3 py-3 text-white"
      >
        <span class="text-sm"><span class="fa fa-envelope px-2"></span>Bundle will expire in</span>
        <span class="text-sm font-bold">${sessionTimeLeft}</span> <!-- Use EL to display session time -->
      </div>

      <!-- Data Balance -->
      <div
        class="bg-white rounded-md flex justify-between items-center m-5 px-3 py-3"
      >
        <span class="text-green-500 text-sm"><span class="fa fa-wifi px-2"></span>ezWiFi Data Balance</span>
        <span class="text-sm font-bold">${remainBytesTotalNice}</span> <!-- Use EL to display data balance -->
      </div>

      <!-- Mambodata Balance -->
      <div
        class="bg-white rounded-md flex justify-between items-center m-5 px-3 py-3"
      >
        <span class="text-green-500 text-sm"><span class="fa fa-mobile px-2"></span>Mambodata Balance</span>
        <span class="text-sm font-bold">0MB</span> <!-- Static example, replace with dynamic value if needed -->
      </div>

      <!-- SMS Balance -->
      <div
        class="bg-white rounded-md flex justify-between items-center m-5 px-3 py-3"
      >
        <span class="text-green-500 text-sm"><span class="fa fa-envelope px-2"></span>Mambodata SMS Balance</span>
        <span class="text-sm font-bold">0SMS</span> <!-- Static example, replace with dynamic value if needed -->
      </div>
    </div>
  </body>
</html>
