# te-table

Developers should use the `te-table` directive when displaying a table/datagrid.  This directive decorates [ng-table](https://github.com/esvit/ng-table) to present common styling/functionality desired across all of our tables.

## Basic usage

```
<te-table data-model="vm.campaigns">
  <table>
    <tr data-ng-repeat="campaigns in $data">
      <td data-title="'global.name' | translate" data-sortable="'name'">{{campaigns.name}}</td>
      <td data-title="'global.advertiser' | translate" data-sortable="'advertiserName'">{{campaigns.advertiserName}}</td>
      <td data-title="'global.brand' | translate" data-sortable="'brandName'">{{campaigns.brandName}}</td>
      <td data-title="'global.domain' | translate" data-sortable="'domain'">{{campaigns.domain}}</td>
    </tr>
  </table>
</te-table>
```

**Note:** *Ignore the `class="aui"` in the above example ([bug in Stash](https://jira.atlassian.com/browse/STASH-3918))*

As previously stated, `te-table` decorates `ng-table`, so everything within the `<table/>` tag conforms to `ng-table`'s API (examples [here](http://ng-table.com/#/)).

There is a caveat in that the developer should be sure to use `$data` in the `data-ng-repeat` declaration.  This is to leverage the table parameters already set up by `te-table`.

## Enhancements to ng-table

### ngTableParams

`te-table` removes the need for the developer to setup an instance of [ngTableParams](https://github.com/esvit/ng-table/wiki/Configuring-your-table-with-ngTableParams) within controllers.  All that is needed is to bind the `data-model` attribute.  This ensures a common experience across all tables.

#### sort

The first sortable column is automatically set as the [sort](https://github.com/esvit/ng-table/wiki/Configuring-your-table-with-ngTableParams#sorting) on the table.

#### defaultSort

The [defaultSort](https://github.com/esvit/ng-table/wiki/Configuring-your-table-with-ngTableParams#defaultsort) setting is configured to `asc`, so that the first click of a column header sorts in ascending order.

#### counts

The [counts](https://github.com/esvit/ng-table/wiki/Configuring-your-table-with-ngTableParams#counts) setting is automatically set by `getData()` in order to prevent paging.

#### getData()

The [getData()](https://github.com/esvit/ng-table/wiki/Configuring-your-table-with-ngTableParams#getdata) function is configured to filter with [gridFilter](http://stash.trueffect.com/projects/CM/repos/cdm-api/browse/ui/client/app/filters/gridFilter/gridFilter.filter.js) for search and [orderBy](https://docs.angularjs.org/api/ng/filter/orderBy) for sorting (see example [here](http://ng-table.com/#/demo/2-1)).

### data-header-title

`ng-table` uses the `data-header-title` attribute on the `<td/>` element to control the `title` attribute of its generated `<th/>` elements.  This attribute defaults to ` `, resulting a blank tooltip on hover (see [TA10759](https://rally1.rallydev.com/#/35683562488d/detail/task/38094367453)).

`te-table` goes through the `<td/>` elements and sets `data-header-title` with the same value as the `data-title` attribute, eliminating the need for developers to copy-and-paste these attributes.

## Model

Setting the data into `te-table` is done with the `data-model` attribute.  This attribute expects that the data is an array.

Example:

```
angular
    .module('uiApp')
    .controller('MyController', MyController);

MyController.$inject = ['MyService'];

function MyController(MyService) {
    var vm = this;

    vm.model = null;
    vm.promise = null;

    activate();

    function activate() {
        var promise = vm.promise = MyService.myServiceCall();

        promise.then(function (dataArray) {
            vm.model = dataArray;
        });
    }
}
```

```
<div cg-busy="vm.promise">
  <te-table data-model="vm.model">
    ...
  </te-table>
</div>
```

Be sure to only assign a non-null to the model after the service calls are resolved.  This is to ensure that `te-table` does not render the table or the "empty" message until after the loading is complete.

## Empty Message

A very common requirement for our tables is to not render the table at all if no data is present, but instead just render a message.  `te-table` handles this with the `data-empty-message` attribute.

```
<te-table data-empty-message="'my.localized.empty.message' | translate">
```

When `data-model` is set with an empty array, this message will be rendered in place of the table.

## Search

Search is a very common requirement for our tables.  `te-table` simplifies search by adding `data-searchable` as a configuration attribute on `<td/>`:

```
<td data-title="'global.name' | translate"
    data-sortable="'name'"
    data-searchable="true">
    {{campaigns.name}}
</td>
```

This attribute leverages `ng-table`'s `data-sortable` and `data-title` configuration attributes, since the most typical use case is to search on the same field that the data is being sorted on.  Setting `data-searchable` to true on a `<td/>` with `data-sortable` will configure `te-table` to search on the same field.  This field will be labeled with the value of the `data-title` attribute in the search configuration dropdown.

In the case that a column needs to be searchable, but not sortable, `data-searchable` will accept the field name, e.g.:

```
<td data-title="'global.name' | translate"
    data-searchable="'name'">
    {{campaigns.name}}
</td>
```

If there are *any* columns specified as searchable, `te-table` will automatically render the search input box.  Otherwise, the input box is hidden.

## Filter by value

`te-table` can filter rows that match allowed values by passing a `filter-values` array of objects with `fieldname` and `values` fields, e.g.:

```
<te-table data-filter-values="vm.filterValues">
  ...
</te-table>
```

```
vm.filterValues = [
            {
                fieldName: 'advertiserName',
                values: [
                    'API QA Test Advertiser',
                    'Advertiser MARE'
                ]
            },
            {
                fieldName: 'brandName',
                values: [
                    'API ',
                    'BRAND MARE'
                ]
            }
        ];
```

By using this filter, the table will only display the rows that match the allowed values on the advertiserName and brandName columns.

## Selection

`te-table` is configured to use Bootstrap's [hover rows](http://getbootstrap.com/css/#tables-hover-rows).  Selecting a row in `te-table` will highlight it with Bootstrap's [.info](http://getbootstrap.com/css/#tables-contextual-classes) contextual class.

### Single-select

The `data-selection-mode` attribute is used to configure how rows are selected in `te-table`.  By default, `te-table` is configured in single selection mode.  This means that only a single row can be selected at any given time.  Clicking on an already selected row will unselect it.

Explicit configuration of single selection mode is as follows:

```
<te-table data-selection-mode="SINGLE">
```

### Multi-select

`te-table` can be configured to allow selection of multiple rows.  This means that any number of rows can be selected.  When configured for multi-select mode, rows include a checkbox at the left to allow for selection.  The header for this checkbox column includes a checkbox to select all displayed rows.

Configuration of multi selection mode is as follows:

```
<te-table data-selection-mode="MULTI">
```

### Doing something on selection

`te-table` exposes the selection through the `data-table-select` directive.  This directive executes the configured function callback and includes the `$selection` local variable, which is the selected row(s).

Example:

```
angular
    .module('uiApp')
    .controller('MyController', MyController);

MyController.$inject = ['$state'];

function MyController($state) {
    var vm = this;

    vm.drillDown = drillDown;

    function drillDown(row) {
        $state.go('my-child-state', row);
    }
}
```

```
<te-table data-te-table-select="vm.drillDown($selection)">
  ...
</te-table>
```

In single selection mode, `$selection` is an object (the object backing the selected row) or `undefined` (if no row is selected), whereas in multi selection mode, it is an array, regardless of how many rows are selected.

## Buttons

Buttons to perform actions are a very common requirement for our tables.  The `te-table` directive recognizes the `te-table-btns` child tag to render buttons in the correct position, to ensure a common experience. It also supports the `te-table-secondary-btns` and `te-table-thirdly-btns` child tags for button placement.

Example:

```
<te-table>
  <te-table-btns>
    ...
  </te-table-btns>
</te-table>
```

### Doing something on click

The `te-table` directive includes the `te-table-btn-click` directive to allow for doing something on button click.  This directive executes the configured function callback and includes the `$selection` local variable, which is the selected row(s).

Example:

```
<te-table>
  <te-table-btns>
    <button class="btn btn-default" data-te-table-btn-click="vm.doSomething($event, $selection)">
      Do something
    </button>
  </te-table-btns>
</te-table>
```

It also includes the `$event` local variable to give your event handlers access to the event object.

### Disabling buttons depending on selection

The `te-table` directive includes the `te-table-btn-selected` and `te-table-btn-single-selected` directives to control disabling buttons depending on whether rows are selected.

The `te-table-btn-selected` directive will disable the button until there are one or more rows selected.

The `te-table-btn-single-selected` directive will disable the button until there is only one row selected.

Example:

```
<te-table>
  <te-table-btns>
    <button class="btn btn-default" data-te-table-btn-selected>
      Enabled when row(s) are selected
    </button>
    <button class="btn btn-default" data-te-table-btn-single-selected>
      Enabled when only one row is selected
    </button>
  </te-table-btns>
</te-table>
```

### te-table-add-btn

The `te-table` directive includes a few commonly used buttons created as their own directives for convenience and for ensuring a common experience.  The `te-table-add-btn` directive renders a button with a [plus icon](http://fortawesome.github.io/Font-Awesome/icon/plus/) for the purpose of adding a record.

```
<te-table>
  <te-table-btns>
    <te-table-add-btn data-te-table-btn-click="vm.addSomething()"></te-table-add-btn>
  </te-table-btns>
</te-table>
```

### te-table-edit-btn

The `te-table-edit-btn` directive renders a button with a [pencil icon](http://fortawesome.github.io/Font-Awesome/icon/pencil/) for the purpose of editing a record.  This button uses `te-table-btn-single-selected` so it is only enabled if and only if a single row is selected.

```
<te-table>
  <te-table-btns>
    <te-table-edit-btn data-te-table-btn-click="vm.editSomething($selection)"></te-table-edit-btn>
  </te-table-btns>
</te-table>
```

### te-table-remove-btn

The `te-table-remove-btn` directive renders a button with a [trash icon](http://fortawesome.github.io/Font-Awesome/icon/trash/) for the purpose of removing record(s).  This button uses `te-table-btn-selected` so it is only enabled if one or more rows are a selected.

```
<te-table>
  <te-table-btns>
    <te-table-remove-btn data-te-table-btn-click="vm.removeSomething($selection)"></te-table-remove-btn>
  </te-table-btns>
</te-table>
```

## Pinned rows

It is possible to "pin" rows to the top of the table by using the `data-pinned-rows` attribute (for instance, the requirement put forth by [US5148](https://rally1.rallydev.com/#/35683562488d/detail/userstory/36975288413)).  The `te-table` directive uses this configuration in the `getData()` call to put rows first in the list.  The object passed into this directive is used as a criteria for _.[where](https://lodash.com/docs#where)().

Example:

```
<te-table data-pinned-rows="{name: 'Default'}">
```

## onCounterSearch

The `onCounterSearch` attribute of the `te-table` tag is a *function* callback to return the current rows on search context that matched

## Attributes

### teTableCustomSearchEnabled

The `teTableCustomSearchEnabled` attribute of the `te-table` tag is a *boolean* to specify if we are going to use custom search instead of the default one.

### teTableOnCustomSearch

The `teTableOnCustomSearch` attribute of the `te-table` tag is a *function* callback to execute custom search operation.

### teTableOnClearSearchField

The `teTableOnClearSearchField` attribute of the `te-table` tag is a *function* callback executed when search input is cleared.

## collapseSearch

The `collapseSearch` attribute of the `te-table` tag is a boolean flag; if set to true it displays a button which shows/hides the search input element with a collapse animation.
