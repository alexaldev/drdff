# drdf

`drdf` is a utility program which computes file differences between 2 (for now) given directories. One main use case is to easily find out which files contained in one directory are missing from another one. 

For example, given the following directory trees: 

.  
├── d1  
│└── foo1  
└── d2  
├── d3  
└── foo3  
└── foo2

`drdf` with search directory *d1* will output *foo1* missing from *d2*. 