<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	tr.header th {font-weight: bold;}
</style>

<f:view>
<a4j:keepAlive beanName="buscaCursoLatoMBean" />
	<h2><ufrn:subSistema /> &gt; Alterar Situação de Proposta</h2>
		<h:form id="form">
  		 <table class="formulario" width="90%">
  		 <h:inputHidden value="#{cursoLatoMBean.obj.propostaCurso.id}" id="proposta"/>
  		 
	  		<caption>Situação de Proposta de Curso Lato Sensu</caption>
	  		  <tbody>
				<tr class="header">
					<th width="130px;">Curso:</th>
					<td>${cursoLatoMBean.obj.nome}</td>
				</tr>
				<tr class="header">
					<th>Situação Atual:</th>
					<td>${cursoLatoMBean.obj.propostaCurso.situacaoProposta.descricao}</td>
				</tr>
			 </tbody>
				<tr>
					<td class="obrigatorio" style="text-align: right; padding-right: 10px;">Nova Situação:</td>
					<td>
						<h:selectOneMenu value="#{cursoLatoMBean.obj.propostaCurso.situacaoProposta.id}" id="situacao">
							<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
							<f:selectItems value="#{situacaoPropostaMBean.allSituacoesValidas}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td class="obrigatorio" style="text-align: right; padding-right: 10px;">Despacho:</td>
					<td>
						<h:inputTextarea id="observacao" value="#{cursoLatoMBean.obj.historicoSituacao.observacoes}" style="width: 95%" rows="10" />
					</td>
				</tr>
		 		<tfoot>
					<tr>
						<td colspan="3">
							<h:commandButton action="#{cursoLatoMBean.alterarSituacao}" value="Alterar" id="buttonAlterar" />
							<h:commandButton action="#{buscaCursoLatoMBean.voltaTelaBusca}" value="<< Voltar" id="buttonVoltar" />
							<h:commandButton action="#{cursoLatoMBean.cancelar}" value="Cancelar" onclick="#{confirm}" id="buttonCancelar" />
						</td>
					</tr>
				</tfoot>
		 </table>
</h:form>

	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br />


</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>