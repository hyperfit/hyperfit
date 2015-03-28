## 1.0.1-SNAPSHOT 
* Initial Versioned release
* Embedded resources are now retrieved via the Links annotation.  If the link is not in the embedded collection then the request to the remote server is made.
* The link annotation now takes just 1 parameter, the link relationship value.  It's also the value of the annotation so no need to specify the param.  IE @Link("bb:promotions") 
* Any method annotated with a Link that returns a boolean is treated as a has Link request.  When true the link is present (either as a link or as embedded) when false the link is not present (either as a link or as embedded)
* The Data annotation now uses value() to set the path, explicitly passing the param as path is not needed.
* any parameter that is provided as null is ignored.
* All resources interfaces now must extend the HyperResource base interface, this exposes the useful hasLink method to check for arbitrary link presence as well as some other lower level stuff to retrieve as needed.
* HyperResource::getLink changed to return a HyperLink instance. 
 * HyperLink's have the following fields (which match the HAL spec's Link Object currently, but new fields outside HAL may be added):
  * href:String
  * rel:String
  * templated:boolean
  * type:String
  * deprecation:String
  * name:String
  * profile:String
  * title:String
  * hrefLang:String
 * href, rel and templated are never null, the other fields are optional so they can be null.
