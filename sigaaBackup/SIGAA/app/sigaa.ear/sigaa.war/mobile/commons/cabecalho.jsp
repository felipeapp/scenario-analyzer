<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">


<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@taglib uri="/tags/sigaa" prefix="sigaa" %>
<%@taglib uri="/tags/sigaaFunctions" prefix="sf"%>


<html xmlns="http://www.w3.org/1999/xhtml" lang="pt" xml:lang="pt">
	
	<head>

		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" /> 

		<style type="text/css">
		
			/* estilo para os botões do menu mobile */
			.botaoMenuMobile{
				width: 170px;  
				background-color: #C4D2EB;
			}
		
		    /* estilo das tabela dos formulario mobile */
			table.formularioMobile{
				border: 0px; 
				padding: 0; 
				margin: 0;
				width: 170px;
				font-size: small;
				border: 1px solid #DEDFE3;
			}
		
			table.formularioMobile th{
				font-size: x-small;
				font-weight: normal;
			}
		
			table.formularioMobile td{
				font-size: x-small;
				font-weight: normal;
			}
		
			table.formularioMobile caption, table.listagemMobile caption {
				margin: 0 auto;
				padding: 3px 0;
				font-weight: bold;
				font-variant: small-caps;
				color: #FFF;
				letter-spacing: 1px;
				font-size: x-small;
				background: url(${ configSistema['linkSigaa'] }/shared/img/bg_caption.gif) center repeat-x;
			}
			
			 /* estilo das tabela de listagem  mobile */
			
			table.listagemMobile{
				font-size:10px; 
				border: 0px; 
				padding: 0; 
				margin: 0; 
				width: 170px;
				border: 1px solid #DEDFE3;
			}
			
			table.listagemMobile th{
				font-size: x-small;
				font-weight: bold;
				text-align: left;
			}
		
			table.formularioMobile td, table.subListagemMobile td{
				font-size: x-small;
				font-weight: normal;
			}
			
			table.listagemMobile td.subListagemMobile{
				font-size: xx-small;
				font-weight: bold;
				margin: 1px 0px;
				letter-spacing: 1px;
				background-image: none;
				background: #EDF1F8;
				color: #333366;
				border-bottom: 1px solid #C8D5EC;
			}
			
			/* as linhas da tabela de listagem */
			.linhaPar {
				background-color: #F9FBFD;
			}
			
			.linhaImpar {
				background-color: #EDF1F8;
			}
			
			
			/* estilo das tabelas dos paremetros da pesquisa */
			table.parametroMobile{
				font-size:10px; 
				border: 0px; 
				padding: 0; 
				margin: 0; 
				width: 170px;
				text-align: right;
			}
		
			table.parametroMobile th{
				font-size: x-small;
				font-weight: bold;
			}
		
			table.parametroMobile td{
				font-size: x-small;
				font-weight: normal;
			}
		
			/* estilo para as legendas das tabelas */
			.infoAltRem {
				background:#F5F5F5 none repeat scroll 0 0;
				border-color:#DEDEDE;
				border-style:solid;
				border-width:1px 0;
				font-weight:bold;
				margin:0 auto 5px;
				padding:2px 0;
				text-align:center;
				width: 170px;
			}
		
			/* estilo para os campos obrigratórios */
			.required {
				background: url(${ configSistema['linkSigaa']}/shared/img/required.gif) no-repeat right 5px;
		  		padding-right: 0.9em;
		  		vertical-align: top;
		  	}
		
			div.obrigatorio{ 
				background: url(${ configSistema['linkSigaa']}/shared/img/required.gif) no-repeat left;
				font-size: x-small;
}
			}
		
		</style>


		<title>${ configSistema['siglaInstituicao'] } Mobile - Academico</title>
	</head>
	
	<body>
	
		<div align="center">
				<img src="${ configSistema['linkSigaa'] }/shared/img/ufrn.gif" alt="logo"/> <br/>
				<small>
				${ configSistema['siglaSigaa'] } Mobile
				</small>
				<br/>
		</div>
		
		<%-- Imprime as mensagens para o usuário do modulo mobile  --%>
		<div style="color:red; font-size:10px; margin-right:auto; margin-left:auto; text-align:center">
			<c:out value="${requestScope.mensagensMobileErro}" />
		</div>
		
		<%-- Imprime as mensagens do modulo web ser exitirem --%>
		<div style=" font-size:10px; margin-right:auto; margin-left:auto; text-align:center" >
			<c:out value="${requestScope.mensagensMobileWarning}" />
		</div>
		
		<%-- Imprime as mensagens do modulo web ser exitirem --%>
		<div style="color:green; font-size:10px; margin-right:auto; margin-left:auto; text-align:center">
			<c:out value="${requestScope.mensagensMobileInformations}" />
		</div>
		
		<br/>
		
		<center>
		
		