<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Beagle</title>
    <link rel="stylesheet" type="text/css" href="assets/lib/perfect-scrollbar/css/perfect-scrollbar.min.css"/>
    <link rel="stylesheet" type="text/css" href="assets/lib/material-design-icons/css/material-design-iconic-font.min.css"/><!--[if lt IE 9]>
    <script src="assets/download/html5shiv.min.js"></script>
    <script src="assets/download/respond.min.js"></script>

    <![endif]-->
    <link rel="stylesheet" href="assets/css/style.css" type="text/css"/>
  </head>
  <body class="be-splash-screen">
  <div class="be-wrapper be-login">
    <div class="be-content">
      <div class="main-content container-fluid">
        <div class="splash-container">
          <div class="panel panel-default panel-border-color panel-border-color-primary">
            <div class="panel-heading"><img src="assets/img/logo-xx.png" alt="logo" width="300" height="50" class="logo-img"></div>
            <div class="panel-body">
              <div class="form-group">
                <input id="username" type="text" placeholder="账号" autocomplete="off" class="form-control">
              </div>
              <div class="form-group">
                <input id="password" type="password" placeholder="密码" class="form-control">
              </div>
              <div class="form-group login-submit">
                <button data-dismiss="modal" onclick="login()" class="btn btn-primary btn-xl">登录</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
    <script src="assets/lib/jquery/jquery.min.js" type="text/javascript"></script>
    <script src="assets/lib/perfect-scrollbar/js/perfect-scrollbar.jquery.min.js" type="text/javascript"></script>
    <script src="assets/js/main.js" type="text/javascript"></script>
    <script src="assets/lib/bootstrap/dist/js/bootstrap.min.js" type="text/javascript"></script>
    <script type="text/javascript">
      $(document).ready(function(){
      	//initialize the javascript
      	App.init();
      });

      function login(){
          $.post({
              type: "post",
              url: "api/doLogin",
              data: {
                  "username": $("#username").val(),
                  "password": $("#password").val()
              },
              dataType: "json",
              success: function (data) {
                  if(data&&data.data==="success"){
                      window.location="manager";
                  }
              }
          });
      }
    </script>
  </body>
</html>