<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao"%>
<script type="text/javascript">
function justificativa() {
	if ($F('form:status') == <%= StatusAvaliacao.AVALIADO_COM_RESSALVAS %>) {
		Element.show('justificativa');
	} else {
		Element.hide('justificativa');
	}
}
</script>

<f:view>

<h2 class="tituloPagina"><ufrn:subSistema /> > Avaliando o Resumo das Atividades do Projeto - Seminário de Iniciação à Docência</h2>

<h:form id="form">
<table class="formulario" width="100%">
		<caption>Resumo para o Seminário de Iniciação à Docência (SID)</caption>
		<tbody>

			<tr>
				<th width="15%"><b>Projeto:</b></th>
				<td>
					<h:outputText value="#{ resumoSid.avaliacaoMonitoria.resumoSid.projetoEnsino.anoTitulo }"/>
					<h:commandLink  title="Visualizar Projeto de Monitoria" action="#{projetoMonitoria.view}" >
					   	<f:param name="id" value="#{resumoSid.avaliacaoMonitoria.resumoSid.projetoEnsino.id}"/>				    	
						<h:graphicImage url="/img/view.gif" />
					</h:commandLink>
					
				</td>
			</tr>
			<tr>
				<th><b>Resumo:</b></th>
				<td align="justify"><h:outputText value="#{ resumoSid.avaliacaoMonitoria.resumoSid.resumo }"/></td>
			</tr>
			<tr>
				<th><b>Ano do SID:</b></th>
				<td><h:outputText value="#{ resumoSid.avaliacaoMonitoria.resumoSid.anoSid }"/></td>
			</tr>
			<tr>
				<th><b>Palavras-Chave:</b></th>
				<td align="justify"><h:outputText value="#{ resumoSid.avaliacaoMonitoria.resumoSid.palavrasChave }"/></td>
			</tr>
			<tr>
				<td colspan="2">
						<table class="subFormulario" width="100%" cellpadding="3">
							<caption>Avaliação</caption>
								<tr>
									<th width="15%"><b>Parecer:</b> </th>
									<td >
											<h:selectOneMenu id="status" onchange="justificativa()" value="#{ resumoSid.avaliacaoMonitoria.statusAvaliacao.id }">
													<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM STATUS --"  />
													<f:selectItems value="#{ resumoSid.statusAvaliacao }"/>
											</h:selectOneMenu>
									</td>
								</tr>

								<tr id="justificativa" style="display: none;">
										<th><b>Justificativa:</b></th>
										<td><h:inputTextarea value="#{ resumoSid.avaliacaoMonitoria.parecer }" style="width:98%" rows="4"/></td>
								</tr>
						</table>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Gravar e Concluir Avaliação Depois" action="#{ resumoSid.gravar }"/>
					<h:commandButton value="Concluir Avaliação" action="#{ resumoSid.avaliar }"/>
					<h:commandButton value="Cancelar" action="#{ resumoSid.cancelar }" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
</table>
</h:form>
</f:view>

<script type="text/javascript">
<!--
	justificativa(); 	
//-->
</script>



<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>