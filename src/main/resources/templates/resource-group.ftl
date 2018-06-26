<@override name="title">
<title>资源池管理</title>
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
    </div>
    <div class="row">
    </div>
    <div class="row">
        <div class="col-sm-6">
            <div class="panel panel-default panel-table">
                <div class="panel-body">
                    <table id="table" class="table table-striped table-hover table-fw-widget">
                        <thead>
                        <tr>
                            <th></th>
                            <th>组名</th>
                            <th>组描述</th>
                            <th>线程数</th>
                            <th>队列长度</th>
                            <th>Host</th>
                            <th>Cluster</th>
                            <th>Active</th>
                            <th>Queued</th>
                            <th>Pool Size</th>
                            <th>Max Active</th>
                            <th>Executions</th>
                            <th>Queue Size</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="col-sm-6">
            <div class="panel panel-default panel-border-color panel-border-color-primary">
                <div class="panel-body">
                    <form data-parsley-validate="" novalidate="" onsubmit="return false;" method="post">
                        <input type="hidden" id="id" name="id">
                        <input type="hidden" id="cloud" name="cloud">
                        <div class="form-group xs-pt-10">
                            <label>组名</label>
                            <input id="groupName" name="groupName" data-parsley-type="alphanum" type="text" required=""
                                   data-parsley-length="[3,20]" placeholder="3-20位字母、数字、下划线、破折号" class="form-control">
                        </div>
                        <div class="form-group xs-pt-10">
                            <label>组描述</label>
                            <input id="groupDesc" name="groupDesc" type="text" required="" data-parsley-length="[3,20]"
                                   placeholder="3-20字符" class="form-control">
                        </div>
                        <div class="form-group">
                            <label>线程数</label>
                            <input id="coreSize" name="coreSize" data-parsley-type="number" type="text" required=""
                                   placeholder="请输入数值" class="form-control">
                        </div>
                        <div class="form-group">
                            <label>队列长度</label>
                            <input id="maxQueueSize" name="maxQueueSize" data-parsley-type="number" type="text"
                                   required=""
                                   placeholder="请输入数值" class="form-control">
                        </div>
                        <div class="row xs-pt-15">
                            <div class="col-xs-6">
                                <span id="successAlert" class="icon mdi " style=" display: none">操作成功</span>
                                <span id="errorAlert" class="icon mdi " style="display: none">操作失败</span>
                            </div>
                            <div class="col-xs-6">
                                <p class="text-right">
                                    <button class="btn btn-space btn-primary">保存</button>
                                </p>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

        </div>
    </div>

</div>

<style>
    td.details-control {
        background: url('assets/img/details_open.png') no-repeat center center;
        cursor: pointer;
    }
    tr.shown td.details-control {
        background: url('assets/img/details_close.png') no-repeat center center;
    }
</style>
</@override>

<@override name="script">
<script src="assets/js/resource-group.js?version=${js_version}" type="text/javascript"></script>
<script src="assets/js/hystrixCommand.js"?version=${js_version} type="text/javascript"></script>
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
            App.initTable();
        });

        $('form').parsley().on('form:submit', function () {
            App.save();
        });

    });

</script>
</@override>
<!--继承的模板要写在最下面-->
<@extends name="common/base.ftl"/>