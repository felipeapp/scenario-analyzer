<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages showDetail="true" />
	<h2> <ufrn:subSistema/> > Relatório Trancamento De Disciplinas</h2>
	<h:form id="form">
	<h:outputText value="#{relatorioDiscente.create}" />
		<table class="formulario" width="50%">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				<tr align="right">
					<th class="required" width="50%">Ano-Período:</th>
					<td align="left" width="2%"><h:inputText value="#{relatorioDiscente.ano}" size="4" maxlength="4" id="ano" onkeyup="return formatarInteiro(this);"/>.
					<h:inputText value="#{relatorioDiscente.periodo}" size="1" maxlength="1" id="periodo" onkeyup="return formatarInteiro(this);"/></td>
				</tr>
				<tr>
					<th>Centro:</th>
					<td><h:selectOneMenu value="#{relatorioDiscente.departamento.id}" id="centroAluno">
						<f:selectItem itemValue="0" itemLabel="--> TODOS <--" />
						<f:selectItems value="#{unidade.centrosEspecificasEscolas}" />
					</h:selectOneMenu></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Gerar Relatório" action="#{relatorioDiscente.gerarRelatorioTrancamentodeDisciplina}" id="btnGerar"/> 
						<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}" id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

 <br />
 <center>
 <h:graphicImage url="/img/required.gif"/>
 <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
 </center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>