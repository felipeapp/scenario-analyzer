<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<f:view>
<h2> <ufrn:subSistema /> &gt; Cadastrar Turma </h2>

<h:messages showDetail="true"></h:messages>

<a4j:keepAlive beanName="turmaInfantilMBean" />
<h:form id="form">
	<h:inputHidden value="#{turmaInfantilMBean.confirmButton}" />
	<h:inputHidden value="#{turmaInfantilMBean.obj.id}" />
	<input type="hidden" name="categoriaMembro" id="categoriaMembro" value="${categoriaAtual}"/>
	
	<table style="width: 70%" class="visualizacao">
		<caption>Dados da Turma</caption>
		
		<tr>
			<th width="15%">Ano:</th>
			<td>
				<h:outputText id="ano" value="#{turmaInfantilMBean.ano}"/>
			</td>
		</tr>
		
		<tr>
			<th>Turma:</th>
			<td>
				<h:outputText id="turma" value="#{turmaInfantilMBean.obj.disciplina.descricaoResumida}"/>
			</td>
		</tr>
		
		<tr>
			<th>Turno:</th>
			<td>
				<h:outputText id="turno" value="#{turmaInfantilMBean.obj.descricaoHorario}"/>
			</td>
		</tr>
		
		<tr>
			<th>Local:</th>
			<td>
				<h:outputText id="local" value="#{turmaInfantilMBean.obj.local}"/>
			</td>
		</tr>
		
		<tr>
			<th>Capacidade:</th>
			<td>
				<h:outputText id="capacidade" value="#{turmaInfantilMBean.obj.capacidadeAluno}"/>
			</td>
		</tr>

		<tr>
			<td class="subFormulario" colspan="2">Professores da Turma</td>
		</tr>

		<tr>
			<td colspan="2">
				<table class="subFormulario" style="width: 100%">
					<tr>
						<td>
							<c:if test="${not empty turmaInfantilMBean.obj.docentesTurmas}">
								<t:dataTable width="100%" var="docenteTurma" value="#{turmaInfantilMBean.obj.docentesTurmas}" styleClass="listagem" id="datatableDocentes" rowIndexVar="indice">
									<t:column>
										<f:facet name="header">
											<f:verbatim><div align="left">Professor(a)</div></f:verbatim>
										</f:facet>
										<h:outputText value="#{ docenteTurma.docente.nome }"/>
										<h:outputText value="#{ docenteTurma.docenteExterno.pessoa.nome }"/>
									</t:column>
									
									<t:column>
										<f:facet name="header">
											<f:verbatim><div align="left">Tipo</div></f:verbatim>
										</f:facet>
										<h:outputText value="#{ docenteTurma.docente != null ? 'Docente' : 'Estagiário'}"/>
									</t:column>
								</t:dataTable>
							</c:if>
						</td>
					</tr>
				</table>
			</td>
		</tr>		
		
		<tfoot>
			<tr>
				<td colspan="4" style="text-align: center">
					<h:commandButton id="btnCadastrar" action="#{turmaInfantilMBean.cadastrar}" value="#{turmaInfantilMBean.confirmButton}"/>
					<h:commandButton id="btnVoltar" action="#{turmaInfantilMBean.voltar}" value="Cancelar" 
						onclick="#{confirm}" immediate="true" rendered="#{ turmaInfantilMBean.obj.id > 0 }" />
					<h:commandButton id="btnCancelar" action="#{turmaInfantilMBean.cancelar}" value="Cancelar" 
						onclick="#{confirm}" immediate="true" rendered="#{ turmaInfantilMBean.obj.id == 0 }"/>
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	
</h:form>

</f:view>

<script type="text/javascript">
	var Tabs = {
	    init : function(){
	        var tabs = new YAHOO.ext.TabPanel('tabs-professores');
	        tabs.addTab('professor-docente', "Docente")
	        tabs.addTab('professor-estagiario', "Estagiário");

	        tabs.activate('professor-docente');	////padrão

		    <c:if test="${sessionScope.aba != null}">
				tabs.activate('${sessionScope.aba}');
			</c:if>
	    }
	}
	YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>