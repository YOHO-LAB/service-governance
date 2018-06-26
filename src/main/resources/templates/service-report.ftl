<@override name="title">
<title>服务统计</title>
</@override>

<@override name="main">
<div class="main-content container-fluid">
    <div class="row">
        <div class="col-sm-2">
            <select class="selectCloud">
                <option value="qCloud1">腾讯云1区</option>
                <option value="qCloud2">腾讯云2区</option>
                <option value="qCloud3">腾讯云3区</option>
            </select>
        </div>
        <#--<div class="col-sm-1">-->
            <#--<span style="height:100%; line-height:48px; text-align:center; float:right;">开始时间</span>-->
        <#--</div>-->
        <#--<div class="col-sm-2">-->
            <#--<div class="input-group date datetimepicker">-->
                <#--<input size="16" readonly value="${date_begin}" id="date_begin" type="text" value=""-->
                       <#--class="form-control"><span class="input-group-addon btn btn-primary">-->
                 <#--<i class="icon-th mdi mdi-calendar"></i></span>-->
            <#--</div>-->
        <#--</div>-->
        <#--<div class="col-sm-1">-->
            <#--<span style="height:100%; line-height:48px; text-align:center; float:right;">结束时间:</span>-->
        <#--</div>-->
        <#--<div class="col-sm-2">-->
            <#--<div class="input-group date datetimepicker">-->
                <#--<input size="16" readonly type="text" value="${date_end}" id="date_end" value=""-->
                       <#--class="form-control"><span class="input-group-addon btn btn-primary">-->
                 <#--<i class="icon-th mdi mdi-calendar"></i></span>-->
            <#--</div>-->
        <#--</div>-->
        <#--<div class="col-sm-1">-->
             <#--<span style="height:100%; line-height:48px; text-align:center;">-->
                 <#--<button class="btn btn-space btn-primary" onclick="App.query();">查询</button>-->
             <#--</span>-->
        <#--</div>-->
    </div>
    <div class="row">
        <div class="col-sm-12">
            <div class="panel panel-default panel-table">
                <div class="panel-body">
                    <table id="table" class="table table-striped table-hover table-fw-widget">
                        <thead>
                        <tr>
                            <th>服务名</th>
                            <th>服务URL</th>
                            <th>压力(毫秒)</th>
                            <th>调用总数</th>
                            <th>并发数(秒)</th>
                            <th>平均耗时(毫秒)</th>
                            <th>错误数</th>
                            <th>90th</th>
                            <th>95th</th>
                            <th>99th</th>
                            <th>调用趋势</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<button style="display:none" data-modal="modal1" id="sendClick" class="btn btn-space btn-success md-trigger">
    Success
</button>


<div id="modal1" class="modal-container colored-header colored-header-primary modal-effect-13">
    <div class="modal-content" style="width:1000px;height: 600px;overflow-y:auto;">
        <div class="modal-header">
            <button type="button" data-dismiss="modal1" id="closeModal1" aria-hidden="true"
                    class="close modal-close"><span
                    class="mdi mdi-close"></span></button>
        </div>
        <div class="modal-body">
            <div class="row">
                <div class="col-sm-12">
                    <div class="panel panel-default panel-table">
                        <div class="panel-body">
                            <table id="table2" class="table table-striped table-hover table-fw-widget" width="100%">
                                <thead>
                                <tr>
                                    <th>ip</th>
                                    <th>请求总数</th>
                                    <th>压力(毫秒)</th>
                                    <th>平均时间开销(毫秒)</th>
                                    <th>并发数(秒)</th>
                                </tr>
                                </thead>
                                <tbody>

                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-12">
                    该数据为最近24小数统计
                    <div class="panel panel-default panel-table">
                        <div class="panel-body">
                            <table id="table3" class="table table-striped table-hover table-fw-widget" width="100%">
                                <thead>
                                <tr>
                                    <th>来源名称</th>
                                    <th>请求次数</th>
                                </tr>
                                </thead>
                                <tbody>

                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


<div id="login">
    <button class="btn btn-space btn-primary" style="float:right;margin-right:30px;" onclick="App.hide();">关闭</button>
    <div class="col-sm-12">
        <div id="line-chart" style="height: 250px;"></div>
    </div>
</div>
<div id="over"></div>
</@override>

<@override name="script">

<script src="assets/js/service-report.js?version=${js_version}" type="text/javascript"></script>
<script src="assets/js/hystrixCommand.js?version=${js_version}" type="text/javascript"></script>
<script src="assets/js/hystrixThreadPool.js?version=${js_version}" type="text/javascript"></script>
<script src="assets/lib/d3.v2.min.js" type="text/javascript"></script>
<script src="assets/lib/jquery.tinysort.min.js" type="text/javascript"></script>
<script src="assets/lib/tmpl.js" type="text/javascript"></script>
<script type="text/javascript">
    $.fn.niftyModal('setDefaults', {
        overlaySelector: '.modal-overlay',
        closeSelector: '.modal-close',
        classAddAfterOpen: 'modal-show',
    });

    $(document).ready(function () {

        App.initTable();

        $(".selectCloud").select2({
            width: '100%'
        });


        $(".selectCloud").on("change", function () {
            App.refreshTable();
        });


        $(".datetimepicker").datetimepicker({
            language: 'zh-CN',
            format: "yyyy-mm-dd hh:ii",
            autoclose: true,
            showMeridian: false,
            componentIcon: '.mdi.mdi-calendar',
            navIcons: {
                rightIcon: 'mdi mdi-chevron-right',
                leftIcon: 'mdi mdi-chevron-left'
            }
        });
    });

</script>
<style>
    #login {
        display: none;
        border: 1em solid #b8c5ea;
        height: 400px;
        width: 100%;
        position: absolute;
        top: 200px;
        z-index: 2;
        background: white;
    }

    #over {
        width: 100%;
        height: 100%;
        opacity: 0.8;
        filter: alpha(opacity=80);
        display: none;
        position: absolute;
        top: 0;
        left: 0;
        z-index: 1;
        background: silver;
    }
</style>
</@override>
<!--继承的模板要写在最下面-->
<@extends name="common/base.ftl"/>