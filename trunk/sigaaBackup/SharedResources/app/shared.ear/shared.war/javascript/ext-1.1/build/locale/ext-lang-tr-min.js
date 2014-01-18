/*
 * Ext JS Library 1.1
 * Copyright(c) 2006-2007, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://www.extjs.com/license
 */


Ext.UpdateManager.defaults.indicatorText="<div class=\"loading-indicator\">Y\xc3\xbckleniyor...</div>";if(Ext.View){Ext.View.prototype.emptyText="";}if(Ext.grid.Grid){Ext.grid.Grid.prototype.ddText="{0} se\xc3\xa7ili sat\xc4\xb1r";}if(Ext.TabPanelItem){Ext.TabPanelItem.prototype.closeText="Bu sekmeyi kapat";}if(Ext.form.Field){Ext.form.Field.prototype.invalidText="Bu alandaki de\xc4\u0178er ge\xc3\xa7ersiz";}Date.monthNames=["Ocak","\xc5\u017eubat","Mart","Nisan","May\xc4\xb1s","Haziran","Temmuz","A\xc4\u0178ustos","Eyl\xc3\xbcl","Ekim","Kas\xc4\xb1m","Aral\xc4\xb1k"];Date.dayNames=["Pazar","Pazartesi","Sal\xc4\xb1","\xc3\u2021ar\xc5\u0178amba","Per\xc5\u0178embe","Cuma","Cumartesi"];if(Ext.MessageBox){Ext.MessageBox.buttonText={ok:"Tamam",cancel:"\xc4\xb0ptal",yes:"Evet",no:"Hay\xc4\xb1r"};}if(Ext.util.Format){Ext.util.Format.date=function(v,_2){if(!v){return"";}if(!(v instanceof Date)){v=new Date(Date.parse(v));}return v.dateFormat(_2||"d/m/Y");};}if(Ext.DatePicker){Ext.apply(Ext.DatePicker.prototype,{todayText:"Bug\xc3\xbcn",minText:"Bu tarih minimum tarihten \xc3\xb6nce",maxText:"Bu tarih maximum tarihten sonra",disabledDaysText:"",disabledDatesText:"",monthNames:Date.monthNames,dayNames:Date.dayNames,nextText:"Sonraki Ay (Ctrl+Sa\xc4\u0178)",prevText:"\xc3\u2013nceki Ay (Ctrl+Sol)",monthYearText:"Bir ay se\xc3\xa7in (Y\xc4\xb1llar\xc4\xb1 de\xc4\u0178i\xc5\u0178tirmek i\xc3\xa7in Ctrl+Yukar\xc4\xb1/A\xc5\u0178a\xc4\u0178\xc4\xb1)",todayTip:"{0} (Bo\xc5\u0178luk)",format:"d/m/y",startDay:1});}if(Ext.PagingToolbar){Ext.apply(Ext.PagingToolbar.prototype,{beforePageText:"Sayfa",afterPageText:" / {0}",firstText:"\xc4\xb0lk Sayfa",prevText:"\xc3\u2013nceki Sayfa",nextText:"Sonraki Sayfa",lastText:"Son Sayfa",refreshText:"Yenile",displayMsg:"{2} sat\xc4\xb1rdan {0} - {1} aras\xc4\xb1 g\xc3\xb6steriliyor",emptyMsg:"G\xc3\xb6sterilecek veri yok"});}if(Ext.form.TextField){Ext.apply(Ext.form.TextField.prototype,{minLengthText:"Bu alan i\xc3\xa7in minimum uzunluk {0}",maxLengthText:"Bu alan i\xc3\xa7in maximum uzunluk {0}",blankText:"Bu alan gerekli",regexText:"",emptyText:null});}if(Ext.form.NumberField){Ext.apply(Ext.form.NumberField.prototype,{minText:"Bu alan i\xc3\xa7in minimum de\xc4\u0178er {0}",maxText:"Bu alan i\xc3\xa7in maximum de\xc4\u0178er {0}",nanText:"{0} ge\xc3\xa7erli bir say\xc4\xb1 de\xc4\u0178il"});}if(Ext.form.DateField){Ext.apply(Ext.form.DateField.prototype,{disabledDaysText:"Pasif",disabledDatesText:"Pasif",minText:"Bu alana {0} tarihinden sonraki bir tarih girilmeli",maxText:"Bu alana {0} tarihinden \xc3\xb6nceki bir tarih girilmeli",invalidText:"{0} ge\xc3\xa7erli bir tarih de\xc4\u0178il - \xc5\u0178u formatta olmal\xc4\xb1 {1}",format:"d/m/y"});}if(Ext.form.ComboBox){Ext.apply(Ext.form.ComboBox.prototype,{loadingText:"Y\xc3\xbckleniyor...",valueNotFoundText:undefined});}if(Ext.form.VTypes){Ext.apply(Ext.form.VTypes,{emailText:"Bu alan bir e-mail adresi format\xc4\xb1nda olmal\xc4\xb1 \"kullanici@alanadi.com\"",urlText:"Bu alan bir URL format\xc4\xb1nda olmal\xc4\xb1 \"http:/"+"/www.alanadi.com\"",alphaText:"Bu alan sadece harf ve _ i\xc3\xa7ermeli",alphanumText:"Bu alan sadece harf, say\xc4\xb1 ve _ i\xc3\xa7ermeli"});}if(Ext.grid.GridView){Ext.apply(Ext.grid.GridView.prototype,{sortAscText:"Artarak S\xc4\xb1rala",sortDescText:"Azalarak S\xc4\xb1rala",lockText:"S\xc3\xbct\xc3\xbcnu Kilitle",unlockText:"S\xc3\xbctunun Kilidini Kald\xc4\xb1r",columnsText:"S\xc3\xbctunlar"});}if(Ext.grid.PropertyColumnModel){Ext.apply(Ext.grid.PropertyColumnModel.prototype,{nameText:"\xc4\xb0sim",valueText:"De\xc4\u0178er",dateFormat:"j/m/Y"});}if(Ext.SplitLayoutRegion){Ext.apply(Ext.SplitLayoutRegion.prototype,{splitTip:"Boyutland\xc4\xb1rmak i\xc3\xa7in s\xc3\xbcr\xc3\xbckleyin.",collapsibleSplitTip:"Boyutland\xc4\xb1rmak i\xc3\xa7in s\xc3\xbcr\xc3\xbckleyin. Gizlemek i\xc3\xa7in \xc3\xa7ift t\xc4\xb1klay\xc4\xb1n."});}