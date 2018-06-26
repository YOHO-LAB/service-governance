<@override name="title">
<title>服务管理</title>
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
        <div class="col-sm-1">
             <span style="height:100%; line-height:48px; text-align:center;">
                 <button class="btn btn-space btn-primary" onclick="App.addService();">新增</button>
             </span>
        </div>
    </div>
    <div class="row">
        <div class="col-sm-12">
            <div class="panel panel-default panel-table">
                <div class="panel-body" style="width: 100%;overflow:scroll">
                    <table id="table" class="table table-striped table-hover table-fw-widget" style="width: 1600px">
                        <thead>
                        <tr>
                            <th style="width: 300px;text-align:center;">服务名</th>
                            <th style="width: 250px;text-align:center;">服务描述</th>
                            <th style="width: 300px;text-align:center;">服务URL</th>
                            <th style="width: 80px;text-align:center;">超时时间</th>
                            <th style="width: 80px;text-align:center;">降级</th>
                            <th style="width: 80px;text-align:center;">限流</th>
                            <th style="width: 160px;text-align:center;">负载均衡策略</th>
                            <th style="width: 150px;text-align:center;">资源池</th>
                            <th style="width: 200px;text-align:center;">操作</th>

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

<button style="display:none" data-modal="modal2" id="sendAddServiceClick" class="btn btn-space btn-success md-trigger">
    addservice
</button>

<button style="display:none" data-modal="modal3" id="sendShowSourceApiClick" class="btn btn-space btn-success md-trigger">
    showSourceApi
</button>
<div id="modal1" class="modal-container colored-header colored-header-primary modal-effect-13">
    <div class="modal-content" style="width:800px;height: 600px;overflow-y:auto;overflow-x:auto;">
        <div class="modal-header">
            <button type="button" data-dismiss="modal1" id="closeModal1" aria-hidden="true"
                    class="close modal-close"><span
                    class="mdi mdi-close"></span></button>
        </div>
        <div class="modal-body">
            <form class="form-horizontal " onsubmit="return false;" method="post">
                <input type="hidden" id="serviceType">
                <div class="form-group ">
                    <label class="col-sm-2 control-label">服务名</label>
                    <div class="col-sm-8">
                        <input type="text" id="serviceName" readonly class="form-control">
                    </div>
                </div>
                <div class="form-group zkShow ">
                    <label class="col-sm-2 control-label">机器列表</label>
                    <div class="col-sm-8">
                        <input type="text" id="ipList" readonly class="form-control">
                    </div>
                </div>
                <div class="form-group serviceUrl">
                    <label class="col-sm-2 control-label">服务URL</label>
                    <div class="col-sm-8">
                        <input type="text" id="serviceUrl" readonly class="form-control">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">服务描述</label>
                    <div class="col-sm-8">
                        <input id="serviceDesc" name="serviceDesc" type="text" class="form-control">
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">超时时间(ms)</label>
                    <div class="col-sm-4">
                        <input id="timeoutMs" name="timeoutMs" type="text" class="form-control">
                    </div>
                    <div class="col-sm-4" style="line-height: 45px;" id="showtimeDiv">
                        (平均:<span id="meanCost"></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;最大:<span id="maxCost"></span>)
                    </div>
                </div>
                <div class="form-group zkShow">
                    <label class="col-sm-2 control-label">降级</label>
                    <div class="col-sm-8">
                        <div class="be-radio has-success inline">
                            <input type="radio" checked="" value="1" name="degrade" id="degradeRadio1">
                            <label for="degradeRadio1">开启</label>
                        </div>
                        <div class="be-radio has-warning inline">
                            <input type="radio" name="degrade" value="0" id="degradeRadio2">
                            <label for="degradeRadio2">关闭</label>
                        </div>
                    </div>
                </div>
                <div class="form-group zkShow">
                    <label class="col-sm-2 control-label">限流</label>
                    <div class="col-sm-8">
                        <div class="be-radio has-success inline">
                            <input type="radio" checked="" name="limit" value="1" id="limitRadio1">
                            <label for="limitRadio1">开启</label>
                        </div>
                        <div class="be-radio has-warning inline">
                            <input type="radio" name="limit" value="0" id="limitRadio2">
                            <label for="limitRadio2">关闭</label>
                        </div>
                    </div>
                </div>
                <div class="form-group zkShow" id="limitSetDiv" style="display: none">
                    <label class="col-sm-2 control-label"></label>
                    <div class="col-sm-8" id="limitSet">

                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-2 control-label">资源池</label>
                    <div class="col-sm-8">
                        <select class="form-control" name="executorGroup" id="executorGroup">

                        </select>
                    </div>
                </div>

                <div class="form-group zkShow">
                    <label class="col-sm-2 control-label">负载策略</label>
                    <div class="col-sm-8">
                        <select class="form-control" name="loadBalanceStrategy" id="loadBalanceStrategy">
                            <option value="random">随机</option>
                            <option value="roundRobin">轮询</option>
                            <option value="roundRobinLocalFirst">轮询(本地优先)</option>
                            <option value="leastActive">最少活跃</option>
                            <option value="weightRoundRobin">权重轮询</option>
                        </select>
                    </div>
                </div>

                <div class="form-group zkShow" id="loadBalanceSetDiv" style="display: none">
                    <label class="col-sm-2 control-label">权重配置</label>
                    <div class="col-sm-8" id="setLoadBalanceStrategy">
                    </div>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" data-dismiss="modal1" class="btn btn-default modal-close">关闭</button>
            <button type="button" data-dismiss="modal1" class="btn btn-default modal-close" onclick="App.save()">修改
            </button>
        </div>
    </div>
</div>

<div id="modal2" class="modal-container colored-header colored-header-primary modal-effect-13">
    <div class="modal-content" style="width:800px;height: 400px;overflow-y:auto;">
        <div class="modal-header">
            <button type="button" data-dismiss="modal2" id="closeModal2" aria-hidden="true"
                    class="close modal-close"><span
                    class="mdi mdi-close"></span></button>
        </div>
        <div class="modal-body">
            <form class="form-horizontal " onsubmit="return false;" method="post">
                <input type="hidden" name="type" id="type">
                <input type="hidden" name="serviceName" id="serviceName">
                <div class="form-group ipList">
                    <label class="col-sm-3 control-label">服务名</label>
                    <div class="col-sm-6">
                        <input type="text" id="serviceName_add" class="form-control">
                    </div>
                </div>
                <div class="form-group serviceUrl">
                    <label class="col-sm-3 control-label">服务URL</label>
                    <div class="col-sm-6">
                        <input type="text" id="serviceUrl_add" class="form-control">
                    </div>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <button type="button" data-dismiss="modal2" onclick="App.saveService()">保存</button>
        </div>
    </div>
</div>

<div id="modal3" class="modal-container colored-header colored-header-primary modal-effect-13">
    <div class="modal-content" style="width:300px;height: 300px;overflow-y:auto;">
        <div class="modal-header">
            <button type="button" data-dismiss="modal3" id="closeModal3" aria-hidden="true"
                    class="close modal-close"><span
                    class="mdi mdi-close"></span></button>
        </div>
        <div class="modal-body">
            <div class="col-sm-8" id="sourceApi">

            </div>
        </div>
    </div>
</div>
</@override>

<@override name="script">
<script src="assets/js/service-manager.js?version=${js_version}" type="text/javascript"></script>

<script type="text/javascript">
    $(document).ready(function () {
        $.fn.niftyModal('setDefaults', {
            overlaySelector: '.modal-overlay',
            closeSelector: '.modal-close',
            classAddAfterOpen: 'modal-show',
        });

        App.initTable();

        $(".selectCloud").select2({
            width: '100%'
        });

        $(".selectCloud").on("change", function () {
            App.refreshTable();
            App.setResourceGroup();
        });

        $("input[name='limit']:radio").change(
                function () {
                    var id = $("input[name='limit']:checked").val();
                    if (id == 1) {
                        $("#limitSetDiv").css("display", "inline");
                    } else {
                        $("#limitSetDiv").css("display", "none");
                    }
                });

        $("#loadBalanceStrategy").change(
                function () {
                    if ($("#loadBalanceStrategy").val() === "weightRoundRobin") {
                        $("#loadBalanceSetDiv").css("display", "inline");
                    } else {
                        $("#loadBalanceSetDiv").css("display", "none");
                    }
                });
        App.setResourceGroup();

        $(".be-datatable-footer").css("width", "1500px");
        $(".be-datatable-header").css("width", "1500px");
    });

</script>
</@override>
<!--继承的模板要写在最下面-->
<@extends name="common/base.ftl"/>