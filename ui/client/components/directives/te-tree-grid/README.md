# te-tree-grid

Developers should use the `te-tree-grid` directive when displaying data in a tree-like structure.  This directive wraps [jqxTreeGrid](https://www.jqwidgets.com/jquery-widgets-demo/demos/jqxtreegrid/index.htm) to present common styling/functionality desired across all of our tables.

## Basic Usage

```
angular
    .module('uiApp')
    .controller('MyController', MyController);

function MyController() {
    var vm = this;

    vm.model = null;

    activate();

    function activate() {
        vm.model = [
            {
                EmployeeID: 2,
                FirstName: 'Andrew',
                LastName: 'Fuller',
                Country: 'USA',
                Title: 'Vice President, Sales',
                HireDate: '1992-08-14 00:00:00',
                BirthDate: '1952-02-19 00:00:00',
                City: 'Tacoma',
                Address: '908 W. Capital Way',
                expanded: 'true',
                children: [
                    {
                        EmployeeID: 8,
                        FirstName: 'Laura',
                        LastName: 'Callahan',
                        Country: 'USA',
                        Title: 'Inside Sales Coordinator',
                        HireDate: '1994-03-05 00:00:00',
                        BirthDate: '1958-01-09 00:00:00',
                        City: 'Seattle',
                        Address: '4726 - 11th Ave. N.E.'
                    },
                    {
                        EmployeeID: 1,
                        FirstName: 'Nancy',
                        LastName: 'Davolio',
                        Country: 'USA',
                        Title: 'Sales Representative',
                        HireDate: '1992-05-01 00:00:00',
                        BirthDate: '1948-12-08 00:00:00',
                        City: 'Seattle',
                        Address: '507 - 20th Ave. E.Apt. 2A'
                    },
                    {
                        EmployeeID: 3,
                        FirstName: 'Janet',
                        LastName: 'Leverling',
                        Country: 'USA',
                        Title: 'Sales Representative',
                        HireDate: '1992-04-01 00:00:00',
                        BirthDate: '1963-08-30 00:00:00',
                        City: 'Kirkland',
                        Address: '722 Moss Bay Blvd.'
                    },
                    {
                        EmployeeID: 4,
                        FirstName: 'Margaret',
                        LastName: 'Peacock',
                        Country: 'USA',
                        Title: 'Sales Representative',
                        HireDate: '1993-05-03 00:00:00',
                        BirthDate: '1937-09-19 00:00:00',
                        City: 'Redmond',
                        Address: '4110 Old Redmond Rd.'
                    },
                    {
                        EmployeeID: 5,
                        FirstName: 'Steven',
                        LastName: 'Buchanan',
                        Country: 'UK',
                        Title: 'Sales Manager',
                        HireDate: '1993-10-17 00:00:00',
                        BirthDate: '1955-03-04 00:00:00',
                        City: 'London',
                        Address: '14 Garrett Hill',
                        expanded: 'true',
                        children: [
                            {
                                EmployeeID: 6,
                                FirstName: 'Michael',
                                LastName: 'Suyama',
                                Country: 'UK',
                                Title: 'Sales Representative',
                                HireDate: '1993-10-17 00:00:00',
                                BirthDate: '1963-07-02 00:00:00',
                                City: 'London',
                                Address: 'Coventry House Miner Rd.'
                            },
                            {
                                EmployeeID: 7,
                                FirstName: 'Robert',
                                LastName: 'King',
                                Country: 'UK',
                                Title: 'Sales Representative',
                                HireDate: '1994-01-02 00:00:00',
                                BirthDate: '1960-05-29 00:00:00',
                                City: 'London',
                                Address: 'Edgeham Hollow Winchester Way'
                            },
                            {
                                EmployeeID: 9,
                                FirstName: 'Anne',
                                LastName: 'Dodsworth',
                                Country: 'UK',
                                Title: 'Sales Representative',
                                HireDate: '1994-11-15 00:00:00',
                                BirthDate: '1966-01-27 00:00:00',
                                City: 'London',
                                Address: '7 Houndstooth Rd.'
                            }
                        ]
                    }
                ]
            }
        ];
    }
}
```

```
<te-tree-grid data-model="vm.model">
  <te-columns>
    <te-column data-field="FirstName"
               data-title="'First Name'"
               data-width="200">
    </te-column>
    <te-column data-field="LastName"
               data-title="'Last Name'"
               data-width="120">
    </te-column>
    <te-column data-field="Title"
               data-width="160">
    </te-column>
    <te-column data-field="BirthDate"
               data-cells-format="'d'"
               data-title="'Birth Date'"
               data-type="date"
               data-width="120">
    </te-column>
    <te-column data-field="HireDate"
               data-cells-format="'d'"
               data-title="'Hire Date'"
               data-type="date"
               data-width="120">
    </te-column>
    <te-column data-field="Address"
               data-width="250">
    </te-column>
    <te-column data-field="City"
               data-width="120">
    </te-column>
    <te-column data-field="Country">
    </te-column>
  </te-columns>
</te-tree-grid>
```

## Example
For an example of the `te-tree-grid` directive being used in production, see:

   * [CDM API/ui/client/app/cm/campaigns/io-tab/io-list/io-list.controller.js](http://stash.trueffect.com/projects/CM/repos/cdm-api/browse/ui/client/app/cm/campaigns/io-tab/io-list/io-list.controller.js)
   * [CDM API/ui/client/app/cm/campaigns/io-tab/io-list/io-list.html](http://stash.trueffect.com/projects/CM/repos/cdm-api/browse/ui/client/app/cm/campaigns/io-tab/io-list/io-list.html)

## Enhancements to jqxTreeGrid

### Configuration through markup

The [jqxTreeGrid](http://www.jqwidgets.com/jquery-widgets-documentation/documentation/jqxtreegrid/jquery-treegrid-getting-started.htm) component is provided by the [jQWidgets](http://www.jqwidgets.com/) library, which provides its own [AngularJS integration](http://www.jqwidgets.com/jquery-widgets-demo/demos/jqxangular/index.htm).  One look at the [documentation](http://www.jqwidgets.com/jquery-widgets-documentation/documentation/angularjs/angularjs.htm) for their integration shows that their configuration is driven by JSON constructed in controllers (e.g. `$scope.settings`).

The [te-tree-grid](http://stash.trueffect.com/projects/CM/repos/cdm-api/browse/ui/client/components/directives/te-tree-grid/te-tree-grid.directive.js) directive provides a way to configure the `jqxTreeGrid` component entirely through markup, removing the need for the developer to instaniate the settings within controllers.  All that is needed is to bind the `data-model` attribute.  This ensures a common experience across all tables.

### Search Field

Out of the box, the `jqxTreeGrid` component provides a search field (demo [here](http://www.jqwidgets.com/jquery-widgets-demo/demos/jqxtreegrid/index.htm#demos/jqxtreegrid/javascript-tree-grid-filter-textbox.htm)).  However, it is very simple in its implementation and does not meet feature parity with the search provided by [te-table](http://stash.trueffect.com/projects/CM/repos/cdm-api/browse/ui/client/components/directives/te-table/README.md).  The `te-tree-grid` directive provides a search field that does, with user-selectable columns for search.

### Sort

The `te-tree-grid` directive provides sorting that is consistent with the experience provided by `te-table`, in that the grid will always be sorted by at least one column, and it is not possible to remove that sort.

Additionally, the first sortable column is implicitly the column the data is initially sorted by.  If an initial sort besides the first sortable column, needs to be explicitly configured this can be accomplished by using the `data-sortable-default` attribute.

### Multi-column sort

Out of the box, the `jqxTreeGrid` component only supports single-column sorting and does **not** expose an API for custom sorting (forum thread [here](http://www.jqwidgets.com/community/topic/treegrid-custom-sort/)).  However, `te-tree-grid` patches the `jqxTreeGrid` component at runtime to allow for multi-column sort (see the `sortHack()` function in [te-tree-grid.controller.js](http://stash.trueffect.com/projects/CM/repos/cdm-api/browse/ui/client/components/directives/te-tree-grid/te-tree-grid.controller.js)).  Whenever a new column is sorted, the last used sort column(s) are used as tiebreakers.

## Attributes

### data-pagination-enabled

The `data-pagination-enabled` attribute of the `te-tree-grid` tag is a *boolean* that allow the component to enable or disable pagination, if attribute is not provided the default value is false.

### data-page-size

The `data-page-size` attribute of the `te-tree-grid` tag is a *numeric* to specify the max number of elements displayed on each page.

### teTreeGridCustomSearch

The `teTreeGridCustomSearch` attribute of the `te-tree-grid` tag is a *boolean* to specify if we are going to use custom search instead of the default one.

### teTreeGridSearchOptions

The `teTreeGridSearchOptions` attribute of the `te-tree-grid` tag is a *object* to define additional configurations to custom search operation: 'searchInterval' and 'searchMinLength'.

### teTreeGridOnSearch

The `teTreeGridOnSearch` attribute of the `te-tree-grid` tag is a *function* callback to execute custom search operation.

### teTreeGridOnClearSearchField

The `teTreeGridOnClearSearchField` attribute of the `te-tree-grid` tag is a *function* callback executed when search input is cleared.

### teTreeGridOnFilterApplied

The `teTreeGridOnFilterApplied` attribute of the `te-tree-grid` tag is a *function* callback executed when a filter is applied, returning an array of all rows that accomplish the filter criteria.

### teTreeGridOnClearFiltering

The `teTreeGridOnClearFiltering` attribute of the `te-tree-grid` tag is a *function* callback executed when a filter is cleaned.

## Columns

The `te-tree-grid` directive provides configuration of columns through the `te-columns` markup tag and its child `te-column` markup tags.

### data-field

The `data-field` attribute of the `te-column` tag is a *string* attribute that specifies the name of the field in the row that should be rendered in the cell.

### data-title

The `data-title` attribute of the `te-column` tag is a *string* attribute that specifies the title that should be rendered in the column header.  This attribute supports filters and one-way binding, and thus literal values should be wrapped in single-quotes (`'`).

### data-filterable

The `data-filterable` attribute of the `te-column` tag is a *boolean* attribute (`true`|`false`, default `false`) that specifies whether the data in the column can be searched.  Columns with this attribute configured as `true` will be labeled with the value of the `data-title` attribute in the search configuration dropdown.

### data-sortable

The `data-sortable` attribute of the `te-column` tag is a *boolean* attribute (`true`|`false`, default `false`) that specifies whether the column is sortable.  The first column with this attribute configured as `true` will implicitly be used as the grid's initial sort, unless there is another column configured with `data-sortable-default` as `true`.

### data-sortable-default

The `data-sortable-default` attribute of the `te-column` tag is a *boolean* attribute (`true`|`false`, default `false`) that allows for explicit configuration of the grid's initial sort.  The first column with this attribute configured as `true` will be used as the grid's initial sort.

### data-cell-class-name

The `data-cell-class-name` attribute of the `te-column` tag exposes the [conditional formatting](http://www.jqwidgets.com/jquery-widgets-demo/demos/jqxtreegrid/index.htm#demos/jqxtreegrid/javascript-tree-grid-conditional-formatting.htm) functionality provided by `jqxTreeGrid`.  The function expression for this attribute exposes the `$cellText`, `$cellValue`, `$dataField`, `$row`, and `$rowData` locals, which you can read more about in the documentation for `jqxTreeGrid`.

### data-cells-align

The `data-cells-align` attribute of the `te-column` tag is a *string* attribute (`left`|`center`|`right`, default `left`) that specifies how the data rendered in the cell should be aligned.  This does **not** affect the alignment of the column header title, which is always *centered*.

### data-cells-format

The `data-cells-format` attribute of the `te-column` tag is a *string* attribute that specifies how the data rendered in the cell should be formatted.  The supported values of this attribute align with the formats provided by [jqxTreeGrid](http://www.jqwidgets.com/jquery-widgets-demo/demos/jqxtreegrid/index.htm#demos/jqxtreegrid/javascript-tree-grid-column-formatting.htm).  This attribute supports filters and one-way binding, and thus literal values should be wrapped in single-quotes (`'`).

### data-cells-renderer

The `data-cells-renderer` attribute of the `te-column` tag exposes the [conditional rendering](http://www.jqwidgets.com/jquery-widgets-demo/demos/jqxtreegrid/index.htm#demos/jqxtreegrid/javascript-tree-grid-cells-renderer.htm) functionality provided by `jqxTreeGrid`.  The function expression for this attribute exposes the `$cellText`, `$cellValue`, `$dataField`, `$row`, and `$rowData` locals, which you can read more about in the documentation for `jqxTreeGrid`.

### data-width

The `data-width` attribute of the `te-column` tag is a *numeric* attribute that specifies either the pixel or percentage width of the column.

## Model

Setting the data into `te-tree-grid` is done with the `data-model` attribute.  This attribute expects that the data is an array.  Child rows are implicitly expected to be specified by the `children` field.  If this field is not available, `te-tree-grid` can be explicitly configured with the `data-children-field` attribute, e.g.:

```
<te-tree-grid data-children-field="someOtherChildRowField">
```

## Selection

At the time of this writing, the `te-tree-grid` directive only supports single selection mode.

### Doing something on selection

`te-tree-grid` exposes the selection through the `data-te-tree-grid-select` directive.  This directive executes the configured function callback and includes the `$selection` and `$level` local varaibles, which are the selected row and level in the hierarchy, respectively.

## Buttons

Buttons to peform actions can be transcluded through the `te-btns` and `te-secondary-btns` child tags, similarly to `te-table`.

## See also

   * http://www.jqwidgets.com/jquery-widgets-demo/demos/jqxtreegrid/index.htm
   * http://www.jqwidgets.com/jquery-widgets-documentation/documentation/jqxtreegrid/jquery-treegrid-getting-started.htm
