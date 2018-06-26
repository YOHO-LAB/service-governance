<#assign js_version = "0.0.6">
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="shortcut icon" href="assets/img/logo-fav.png">

    <@block name="title"></@block>
    <link rel="stylesheet" type="text/css" href="assets/lib/datatables/css/jquery.dataTables.min.css"/>
    <link rel="stylesheet" type="text/css"
          href="assets/lib/material-design-icons/css/material-design-iconic-font.min.css"/>
    <!--[if lt IE 9]>
    <script src="assets/download/html5shiv.min.js"></script>
    <script src="assets/download/respond.min.js"></script>
    <![endif]-->
    <link rel="stylesheet" type="text/css" href="assets/lib/datetimepicker/css/bootstrap-datetimepicker.min.css"/>
    <link rel="stylesheet" type="text/css" href="assets/lib/bootstrap-slider/css/bootstrap-slider.css"/>
    <link rel="stylesheet" type="text/css" href="assets/lib/select2/css/select2.min.css"/>
    <link rel="stylesheet" type="text/css" href="assets/lib/confirm/jquery-confirm.min.css"/>
    <link rel="stylesheet" type="text/css" href="assets/lib/morrisjs/morris.css"/>


    <link rel="stylesheet" href="assets/css/style.css" type="text/css"/>

</head>
<body>
<div class="be-wrapper">
    <nav class="navbar navbar-default navbar-fixed-top be-top-header">
        <nav class="navbar navbar-default navbar-fixed-top be-top-header">
            <div class="container-fluid">
                <div class="navbar-header"><span class="navbar-brand"
                                                 style="width: 300px;height: 60px;background-size:280px 50px;background-position:5px center;"></span>
                </div>
            </div>
        </nav>
    </nav>
    <div class="be-left-sidebar" style="width: 200px">
        <div class="left-sidebar-wrapper"><a href="#" class="left-sidebar-toggle">General Tables</a>
            <div class="left-sidebar-spacer">
                <div class="left-sidebar-scroll">
                    <div class="left-sidebar-content">
                        <ul class="sidebar-elements">

                            <li class="parent"><a href="#"><i class="icon mdi mdi-home"></i><span>服务治理</span></a>
                                <ul class="sub-menu">
                                    <li id="service-manager"><a href="manager">服务管理</a>
                                    </li>
                                    <li id="service-report"><a href="report">服务统计</a>
                                    </li>
                                    <li id="service-errors"><a href="errors">服务错误</a>
                                    </li>
                                    <li id="resource-group"><a href="resourceGroup">资源组管理</a>
                                    </li>
                                </ul>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="be-content" style="margin-left:200px">
        <!--页面主体，子页面实现-->
        <@block  name="main"></@block>
    </div>
</div>
<!--公共JS-->
<script src="assets/lib/jquery/jquery.min.js" type="text/javascript"></script>
<script src="assets/lib/perfect-scrollbar/js/perfect-scrollbar.jquery.min.js" type="text/javascript"></script>
<script src="assets/js/main.js" type="text/javascript"></script>
<script src="assets/lib/bootstrap/dist/js/bootstrap.min.js" type="text/javascript"></script>
<script src="assets/lib/datatables/js/jquery.dataTables.js" type="text/javascript"></script>
<script src="assets/lib/datatables/js/dataTables.bootstrap.min.js" type="text/javascript"></script>
<script src="assets/lib/datatables/js/fnReloadAjax.js" type="text/javascript"></script>
<script src="assets/lib/datatables/plugins/buttons/js/dataTables.buttons.js" type="text/javascript"></script>
<script src="assets/lib/datatables/plugins/buttons/js/buttons.html5.js" type="text/javascript"></script>
<script src="assets/lib/datatables/plugins/buttons/js/buttons.flash.js" type="text/javascript"></script>
<script src="assets/lib/datatables/plugins/buttons/js/buttons.print.js" type="text/javascript"></script>
<script src="assets/lib/datatables/plugins/buttons/js/buttons.colVis.js" type="text/javascript"></script>
<script src="assets/lib/datatables/plugins/buttons/js/buttons.bootstrap.js" type="text/javascript"></script>
<script src="assets/lib/jquery.niftymodals/dist/jquery.niftymodals.js" type="text/javascript"></script>
<script src="assets/lib/jquery-ui/jquery-ui.min.js" type="text/javascript"></script>
<script src="assets/lib/jquery.nestable/jquery.nestable.js" type="text/javascript"></script>
<script src="assets/lib/moment.js/min/moment.min.js" type="text/javascript"></script>
<script src="assets/lib/datetimepicker/js/bootstrap-datetimepicker.min.js" type="text/javascript"></script>
<script src="assets/lib/datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN.js" type="text/javascript"></script>
<script src="assets/lib/select2/js/select2.min.js" type="text/javascript"></script>
<script src="assets/lib/bootstrap-slider/js/bootstrap-slider.js" type="text/javascript"></script>
<script src="assets/lib/parsley/parsley.min.js" type="text/javascript"></script>
<script src="assets/lib/confirm/jquery-confirm.min.js" type="text/javascript"></script>
<script src="assets/lib/raphael/raphael-min.js" type="text/javascript"></script>
<script src="assets/lib/morrisjs/morris.min.js" type="text/javascript"></script>
<script>
    $(function () {
        $.ajaxSetup({
            type: "POST",
            error: function (jqXHR, textStatus, errorThrown) {
                switch (jqXHR.status) {
                    case(401):
                        window.location = "login";
                        break;
                    default:
                        alert("未知错误");
                }
            }
        });
    });
    $.fn.dataTable.ext.errMode = 'throw';
</script>

<script>
    $(document).ready(function () {
        $("#${pageName}").addClass("active");
        App.init();
    });
</script>
<!--各自页面JS-->
<@block  name="script"></@block>

</body>
</html>