<@override name="title">
<title>服务错误</title>
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
         <div class="col-sm-12">
             <div class="panel panel-default panel-table">
                 <div class="panel-body">
                     <table id="table" class="table table-striped table-hover table-fw-widget">
                         <thead>
                         <tr>
                             <th></th>
                             <th>时间</th>
                             <th>调用方</th>
                             <th>来源接口</th>
                             <th>来源IP</th>
                             <th>目标服务</th>
                             <th>目标IP</th>
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
    <script src="assets/js/service-errors.js?version=${js_version}" type="text/javascript"></script>
    <script type="text/javascript">
        $.fn.niftyModal('setDefaults',{
            overlaySelector: '.modal-overlay',
            closeSelector: '.modal-close',
            classAddAfterOpen: 'modal-show',
        });
        $(document).ready(function(){

            App.initTable();

            $('#table tbody').on('click', 'td.details-control', function () {
                var tr = $(this).closest('tr');
                var row = App.table.row(tr);
                if (row.child.isShown()) {
                    row.child.hide();
                    tr.removeClass('shown');
                }
                else {
                    row.child(App.format(row.data())).show();
                    tr.addClass('shown');
                }
            });

            $(".selectCloud").select2({
                width: '100%'
            });

            $(".selectCloud").on("change", function () {
                App.initTable();
            });
        });
    </script>
</@override>
<!--继承的模板要写在最下面-->
<@extends name="common/base.ftl"/>