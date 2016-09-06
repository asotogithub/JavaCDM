# te-tree

The `te-tree` directive dsplays data in a tree-like structure for a single column, allowing callback functions for drag and drop events with [angular-dragdrop] (https://github.com/codef0rmer/angular-dragdrop). This directive wraps [angular-ui-tree](https://github.com/angular-ui-tree/angular-ui-tree) and has added styling/functionality that is desired across all of our tables.

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
        vm.header = 'My tree';
        vm.model = [
            {
                id: 1,
                name: 'node1',
                children: [
                    {
                        id: 11,
                        name: 'node1.1',
                        children: [
                            {
                                id: 111,
                                name: 'node1.1.1',
                                children: []
                            }
                        ]
                    },
                    {
                        id: 12,
                        name: 'node1.2',
                        children: []
                    }
                ]
            },
            {
                id: 2,
                name: 'node2',
                children: [
                    {
                        id: 21,
                        name: 'node2.1',
                        children: []
                    },
                    {
                        id: 22,
                        name: 'node2.2',
                        children: []
                    }
                ]
            },
            {
                id: 3,
                name: 'node3',
                children: [
                    {
                        id: 31,
                        name: 'node3.1',
                        children: []
                    }
                ]
            }
        ];
    }
}
```

```
<te-tree data-header="vm.header"
         data-model="vm.model">
</te-tree>
```

## Attributes

### data-header

The `data-header` attribute of the `te-tree` tag is a *string* to be displayed as a header for the tree. If this attribute is not provided, the header is not displayed. 

### data-on-drop

The `data-on-drop` attribute of the `te-tree` tag is a *function* callback executed when a `jqyoui-draggable` element is dropped on a row.

### data-on-expand-collapse

The `data-on-expand-collapse` attribute of the `te-tree` tag is a *function* callback executed when a row with children is expanded/collapsed.

### data-on-select

The `data-on-select` attribute of the `te-tree` tag is a *function* callback executed when a row is selected/clicked on.

## Buttons

Buttons to peform actions can be transcluded through the `te-btns` and `te-secondary-btns` child tags, similarly to `te-table`.
