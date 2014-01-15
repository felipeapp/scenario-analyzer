<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="relatoriosDepartamentoCpdi"></a4j:keepAlive>
	<h:outputText value="#{relatoriosDepartamentoCpdi.create}" />
	<%@include file="/portais/docente/menu_docente.jsp"%>

<h2><ufrn:subSistema /> > ${relatoriosDepartamentoCpdi.titulo} </h2>
<div id="ajuda" class="descricaoOperacao">
		<p>Caro Usu�rio, </p>
		<p>
		esta opera��o permite ao gestor gerar um relat�rio com os dados quantitativos dos departamentos, cursos, docentes, 
		discentes, t�cnicos administrativos, teses/disserta��es, bases de pesquisa e projetos de extens�o associados 
		para unidade acad�mica de acordo com o ano e per�odo selecionado. 
		</p>
		<p>
			Segue abaixo mais informa��es de como � contabilizado os dados quantitativos em rela��o ao ano e per�odo selecionado:
			<ul>
				<li><b>Departamentos:</b>Todos departamentos, n�o extintos, criados at� o ano e per�odo informado.</li>
				<li><b>Cursos de P�s-Gradua��o Stricto Sensu:</b>Todos os cursos reconhecidos at� o ano e per�odo informado;</li>
				<li>
					<b>Cursos de P�s-Gradua��o Lato Sensu:</b>Todos os cursos, com situa��o da proposta 
					aceita, n�o finalizados, que tenham iniciado at� o ano e per�odo informado.
				</li>
				<li><b>Cursos de Gradua��o:</b>Todos os cursos criados at� o ano e per�odo informado;</li>
				<li><b>Docentes:</b>Todos os docentes onde o ano e per�odo informado dentro do intervalo da data de admiss�o e a data de desligamento. </li>
				<li>
					<b>Discentes:</b> Todos os discentes ingressantes at� o ano e per�odo informado. No caso  
					dos matriculados, o ano e per�odo da matr�cula no componente tamb�m � levado em considera��o;
				</li>
				<li><b>Teses/Disserta��o:</b>Todas as teses/disserta��es com banca cadastrada at� o ano e per�odo informado. </li>
				<li><b>Bases de Pesquisa:</b>Todas as bases de pesquisa criadas at� o ano e per�odo informado.</li> 
				<li><b>Projetos de Extens�o:</b>Todos os projetos em execu�ao, conclu�dos ou aprovados, onde o ano e per�odo informados est�o dentro do intervalo da data inicial e final do projeto de extens�o. </li>
			</ul>  
		</p>
</div>
<h:form id="form">
	
	<table class="formulario" style="width: 95%">
	<caption> Informe os crit�rios para a emiss�o do relat�rio </caption>
		<tr>
			<th><b>Centro:</b></th>
			<td>
				${usuario.vinculoAtivo.unidade.unidadeGestora.codigoNome}
			</td>
		</tr>
		
		<c:if test="${relatoriosDepartamentoCpdi.necessitaPeriodo}">
		<tr>
			<th class="required">Ano.Per�odo:</th>
			<td>
				<h:inputText id="ano" value="#{relatoriosDepartamentoCpdi.ano}" 
					size="4" maxlength="4" onkeyup="formatarInteiro(this)" />
				.<h:inputText id="semestre" value="#{relatoriosDepartamentoCpdi.periodo}" size="1"
					maxlength="1" onkeyup="formatarInteiro(this)" />
			</td>
		</tr>
		</c:if>
		
		<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton action="#{relatoriosDepartamentoCpdi.gerarRelatorioGerencial}" value="Emitir Relat�rio"/>
				<h:commandButton action="#{relatoriosDepartamentoCpdi.cancelar}" value="Cancelar"/>
			</td>
		</tr>
		</tfoot>
	</table>
		
</h:form>

<br/>
<center>
	<html:img page="/img/required.gif" style="vertical-align: top;" /> 
	<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
</center>	

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>