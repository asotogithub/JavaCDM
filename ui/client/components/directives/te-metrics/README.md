# TE Metrics Module

This module was created as a consistent way to display key perfomance indicators across the project.  These indicators usually give information
around camapign performance but can be leveraged for other tasks.  The legend, key, value and icons are variable based on the input.

## Usage

    <te-metrics data-model="DATA_BINDING" data-legend="LEGEND"></te-metrics>

Where `DATA_BINDING` is a JSON array conforming to the following specification:

    {
        key: String,
        value: String,
        icon: String, // font-awesome icon name
        type: 'number|percentage|currency' // omit this for no formatting
    }

Usage Example:

    {
        key: 'Impressions',
        value: Math.floor(Math.random() * 10000 + 1),
        icon: 'fa-eye',
        type: 'number'
    }
