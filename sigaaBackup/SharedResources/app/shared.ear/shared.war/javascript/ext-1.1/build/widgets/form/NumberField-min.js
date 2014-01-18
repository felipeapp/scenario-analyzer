/*
 * Ext JS Library 1.1
 * Copyright(c) 2006-2007, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://www.extjs.com/license
 */


Ext.form.NumberField=function(_1){Ext.form.NumberField.superclass.constructor.call(this,_1);};Ext.extend(Ext.form.NumberField,Ext.form.TextField,{fieldClass:"x-form-field x-form-num-field",allowDecimals:true,decimalSeparator:".",decimalPrecision:2,allowNegative:true,minValue:Number.NEGATIVE_INFINITY,maxValue:Number.MAX_VALUE,minText:"The minimum value for this field is {0}",maxText:"The maximum value for this field is {0}",nanText:"{0} is not a valid number",initEvents:function(){Ext.form.NumberField.superclass.initEvents.call(this);var _2="0123456789";if(this.allowDecimals){_2+=this.decimalSeparator;}if(this.allowNegative){_2+="-";}this.stripCharsRe=new RegExp("[^"+_2+"]","gi");var _3=function(e){var k=e.getKey();if(!Ext.isIE&&(e.isSpecialKey()||k==e.BACKSPACE||k==e.DELETE)){return;}var c=e.getCharCode();if(_2.indexOf(String.fromCharCode(c))===-1){e.stopEvent();}};this.el.on("keypress",_3,this);},validateValue:function(_7){if(!Ext.form.NumberField.superclass.validateValue.call(this,_7)){return false;}if(_7.length<1){return true;}var _8=this.parseValue(_7);if(isNaN(_8)){this.markInvalid(String.format(this.nanText,_7));return false;}if(_8<this.minValue){this.markInvalid(String.format(this.minText,this.minValue));return false;}if(_8>this.maxValue){this.markInvalid(String.format(this.maxText,this.maxValue));return false;}return true;},getValue:function(){return this.fixPrecision(this.parseValue(Ext.form.NumberField.superclass.getValue.call(this)));},parseValue:function(_9){return parseFloat(String(_9).replace(this.decimalSeparator,"."))||"";},fixPrecision:function(_a){var _b=isNaN(_a);if(!this.allowDecimals||this.decimalPrecision==-1||_b||!_a){return _b?"":_a;}var _c=Math.pow(10,this.decimalPrecision+1);var _d=this.decimalPrecisionFcn(_a*_c);_d=this.decimalPrecisionFcn(_d/10);return _d/(_c/10);},decimalPrecisionFcn:function(v){return Math.floor(v);},beforeBlur:function(){var v=this.parseValue(this.getRawValue());if(v){this.setValue(this.fixPrecision(v));}}});