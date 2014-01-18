/*
 * Ext JS Library 1.1
 * Copyright(c) 2006-2007, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://www.extjs.com/license
 */


Ext.UpdateManager.defaults.indicatorText="<div class=\"loading-indicator\">Laddar...</div>";if(Ext.View){Ext.View.prototype.emptyText="";}if(Ext.grid.Grid){Ext.grid.Grid.prototype.ddText="{0} markerade rad(er)";}if(Ext.TabPanelItem){Ext.TabPanelItem.prototype.closeText="St\xc3\xa4ng denna tabb";}if(Ext.form.Field){Ext.form.Field.prototype.invalidText="V\xc3\xa4rdet i detta f\xc3\xa4lt \xc3\xa4r inte till\xc3\xa5tet";}if(Ext.LoadMask){Ext.LoadMask.prototype.msg="Laddar...";}Date.monthNames=["Januari","Februari","Mars","April","Maj","Juni","Juli","Augusti","September","Oktober","November","December"];Date.dayNames=["S\xc3\xb6ndag","M\xc3\xa5ndag","Tisdag","Onsdag","Torsdag","Fredag","L\xc3\xb6rdag"];if(Ext.MessageBox){Ext.MessageBox.buttonText={ok:"OK",cancel:"Avbryt",yes:"Ja",no:"Nej"};}if(Ext.util.Format){Ext.util.Format.date=function(v,_2){if(!v){return"";}if(!(v instanceof Date)){v=new Date(Date.parse(v));}return v.dateFormat(_2||"Y-m-d");};}if(Ext.DatePicker){Ext.apply(Ext.DatePicker.prototype,{todayText:"Idag",minText:"Detta datum intr\xc3\xa4ffar f\xc3\xb6re det tidigast till\xc3\xa5tna",maxText:"Detta datum intr\xc3\xa4ffar efter det senast till\xc3\xa5tna",disabledDaysText:"",disabledDatesText:"",monthNames:Date.monthNames,dayNames:Date.dayNames,nextText:"N\xc3\xa4sta M\xc3\xa5nad (Ctrl + h\xc3\xb6ger piltangent)",prevText:"F\xc3\xb6reg\xc3\xa5ende M\xc3\xa5nad (Ctrl + v\xc3\xa4nster piltangent)",monthYearText:"V\xc3\xa4lj en m\xc3\xa5nad (Ctrl + Upp\xc3\xa5t/Ner\xc3\xa5t pil f\xc3\xb6r att \xc3\xa4ndra \xc3\xa5rtal)",todayTip:"{0} (Mellanslag)",format:"y-m-d",startDay:1});}if(Ext.PagingToolbar){Ext.apply(Ext.PagingToolbar.prototype,{beforePageText:"Sida",afterPageText:"av {0}",firstText:"F\xc3\xb6rsta sidan",prevText:"F\xc3\xb6reg\xc3\xa5ende sida",nextText:"N\xc3\xa4sta sida",lastText:"Sista sidan",refreshText:"Uppdatera",displayMsg:"Visar {0} - {1} av {2}",emptyMsg:"Det finns ingen data att visa"});}if(Ext.form.TextField){Ext.apply(Ext.form.TextField.prototype,{minLengthText:"Minsta till\xc3\xa5tna l\xc3\xa4ngd f\xc3\xb6r detta f\xc3\xa4lt \xc3\xa4r {0}",maxLengthText:"St\xc3\xb6rsta till\xc3\xa5tna l\xc3\xa4ngd f\xc3\xb6r detta f\xc3\xa4lt \xc3\xa4r {0}",blankText:"Detta f\xc3\xa4lt \xc3\xa4r obligatoriskt",regexText:"",emptyText:null});}if(Ext.form.NumberField){Ext.apply(Ext.form.NumberField.prototype,{minText:"Minsta till\xc3\xa5tna v\xc3\xa4rde f\xc3\xb6r detta f\xc3\xa4lt \xc3\xa4r {0}",maxText:"St\xc3\xb6rsta till\xc3\xa5tna v\xc3\xa4rde f\xc3\xb6r detta f\xc3\xa4lt \xc3\xa4r {0}",nanText:"{0} \xc3\xa4r inte ett till\xc3\xa5tet nummer"});}if(Ext.form.DateField){Ext.apply(Ext.form.DateField.prototype,{disabledDaysText:"Inaktiverad",disabledDatesText:"Inaktiverad",minText:"Datumet i detta f\xc3\xa4lt m\xc3\xa5ste intr\xc3\xa4ffa efter {0}",maxText:"Datumet i detta f\xc3\xa4lt m\xc3\xa5ste intr\xc3\xa4ffa f\xc3\xb6re {0}",invalidText:"{0} \xc3\xa4r inte ett till\xc3\xa5tet datum - datum skall anges i formatet {1}",format:"y/m/d"});}if(Ext.form.ComboBox){Ext.apply(Ext.form.ComboBox.prototype,{loadingText:"Laddar...",valueNotFoundText:undefined});}if(Ext.form.VTypes){Ext.apply(Ext.form.VTypes,{emailText:"Detta f\xc3\xa4lt skall vara en e-post adress i formatet \"user@domain.com\"",urlText:"Detta f\xc3\xa4lt skall vara en l\xc3\xa4nk (URL) i formatet \"http:/"+"/www.domain.com\"",alphaText:"Detta f\xc3\xa4lt f\xc3\xa5r bara inneh\xc3\xa5lla bokst\xc3\xa4ver och \"_\"",alphanumText:"Detta f\xc3\xa4lt f\xc3\xa5r bara inneh\xc3\xa5lla bokst\xc3\xa4ver, nummer och \"_\""});}if(Ext.grid.GridView){Ext.apply(Ext.grid.GridView.prototype,{sortAscText:"Sortera stigande",sortDescText:"Sortera fallande",lockText:"L\xc3\xa5s kolumn",unlockText:"L\xc3\xa5s upp kolumn",columnsText:"Kolumner"});}if(Ext.grid.PropertyColumnModel){Ext.apply(Ext.grid.PropertyColumnModel.prototype,{nameText:"Namn",valueText:"V\xc3\xa4rde",dateFormat:"Y/m/d"});}if(Ext.SplitLayoutRegion){Ext.apply(Ext.SplitLayoutRegion.prototype,{splitTip:"Dra f\xc3\xb6r att \xc3\xa4ndra storleken.",collapsibleSplitTip:"Drag f\xc3\xb6r att \xc3\xa4ndra storleken. Dubbelklicka f\xc3\xb6r att g\xc3\xb6mma."});}