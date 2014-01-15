<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDiscente.create}"></h:outputText>
<h2><ufrn:subSistema /> > Relatório de Alunos com Mobilidade Estudantil</h2>

<div class="descricaoOperacao">
	<p> <strong>Caro Usuário,</strong> </p>

	<p>
		Esta operação destina-se ao relatório de Mobilidades Estudantis ativas dentro do período selecionado, 
		para gerar o relatório informe abaixo o prazo das mobilidades estudantis que serão atendidas no relatório.
	</p>
</div>

<h:form id="form">
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th width="50%">Ano-Período Inicial: </th>	
		<td width="46%">	
			<h:inputText value="#{relatorioDiscente.ano}" size="4" maxlength="4" id="ano" onkeyup="return formatarInteiro(this);"/> .
			<h:inputText value="#{relatorioDiscente.periodo}" size="1" maxlength="1" id="periodo"  onkeyup="return formatarInteiro(this);"/>
		</td>		
	</tr>
	<tr>
		<th width="50%">Ano-Período Final:</th> 
		<td width="46%">
			<h:inputText value="#{relatorioDiscente.anoFim}" size="4" maxlength="4" id="anoFim" onkeyup="return formatarInteiro(this);" /> .
			<h:inputText value="#{relatorioDiscente.periodoFim}" size="1" maxlength="1"	id="periodoFim" onkeyup="return formatarInteiro(this);" />
		</td>	
	</tr>
	<tr>
		<td width="100%" colspan="2" align="center">
			<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
				action="#{relatorioDiscente.gerarRelatorioMobilidade}"/>
			<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" />
		</td>
	</tr>

</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>