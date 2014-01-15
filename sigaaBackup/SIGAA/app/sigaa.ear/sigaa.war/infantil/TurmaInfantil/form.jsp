<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style type="text/css">
<!--
table.niveis tr td {
	padding: 0px;
}
-->
</style>

<f:view>
<h2> <ufrn:subSistema /> &gt; ${turmaInfantilMBean.confirmButton} Turma </h2>

<h:messages showDetail="true"></h:messages>

<a4j:keepAlive beanName="turmaInfantilMBean" />
<h:form id="form">
	<h:inputHidden value="#{turmaInfantilMBean.confirmButton}" />
	<h:inputHidden value="#{turmaInfantilMBean.obj.id}" />
	<input type="hidden" name="categoriaMembro" id="categoriaMembro" value="${categoriaAtual}"/>
	
	<table class="formulario">
		<caption>Dados da Turma</caption>
		
		<tr>
			<th class="obrigatorio">Ano:</th>
			<td>
				<h:inputText id="ano" value="#{turmaInfantilMBean.ano}" size="4" maxlength="4" onkeyup="formatarInteiro(this);" />
			</td>
		</tr>
		
		<tr>
			<th class="obrigatorio">Turma:</th>
			<td>
				<h:selectOneMenu id="nivel" value="#{turmaInfantilMBean.obj.disciplina.id}" >
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
					<f:selectItems value="#{turmaInfantilMBean.niveisCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
		
		<tr>
			<th class="obrigatorio">Turno:</th>
			<td>
				<h:selectOneMenu id="turno" value="#{turmaInfantilMBean.obj.descricaoHorario}" >
					<f:selectItem itemLabel="-- SELECIONE --" itemValue=""/>
					<f:selectItem itemLabel="Matutino" itemValue="M"/>
					<f:selectItem itemLabel="Vespertino" itemValue="T"/>
				</h:selectOneMenu>
			</td>
		</tr>
		
		<tr>
			<th class="obrigatorio">Local:</th>
			<td>
				<h:inputText id="local" value="#{turmaInfantilMBean.obj.local}" size="50" maxlength="60" />
			</td>
		</tr>
		
		<tr>
			<th class="obrigatorio">Capacidade:</th>
			<td>
				<h:inputText id="capacidade" value="#{turmaInfantilMBean.obj.capacidadeAluno}" size="3" maxlength="3" onkeyup="formatarInteiro(this);"/>
			</td>
		</tr>

		<tr>
			<td class="subFormulario" colspan="2">Professores da Turma</td>
		</tr>
		
		<tr>
			<td colspan="2">
       			<table class="subFormulario" width="100%">
        			<caption> Selecione o(s) Docente(s) para a Turma </caption>
        				<tr>
							<td colspan="2">
								<div id="tabs-membro">
									<div id="membro-docente" >
										<table width="100%">
											<tr>
												<th class="obrigatorio" width="26%">Docente:</th>
												<td nowrap="nowrap">
													<h:inputText id="nomeDocente" value="#{ turmaInfantilMBean.docenteTurmaInfantil.docente.pessoa.nome }" size="70" 
														onfocus="$('categoriaMembro').value=#{ categoriaMembro.DOCENTE }" />
													<rich:suggestionbox for="nomeDocente" id="suggestion_docente"  width="430" height="100" minChars="3" 
													    suggestionAction="#{ servidorAutoCompleteMBean.autocompleteNomeServidor }" var="_docente" 
													    fetchValue="#{ _docente.pessoa.nome }" onsubmit="$('indicatorDocente').style.display='';" 
													    oncomplete="$('indicatorDocente').style.display='none';">
													  	
													  	<f:param name="apenasAtivos" value="true" />
											    	  	<f:param name="apenasDocentes" value="true" />
												      	
												      	<h:column>
													      	<h:outputText value="#{ _docente.nomeSiape }"/>
												      	</h:column>
												      	
														<a4j:support event="onselect" reRender="btnAdicionarDocente">
														  	<f:param name="apenasAtivos" value="true" />
												    	  	<f:param name="apenasDocentes" value="true" />
															<f:setPropertyActionListener value="#{ _docente.id }" target="#{ turmaInfantilMBean.docenteTurmaInfantil.docente.id }" />
														</a4j:support>
													</rich:suggestionbox>
													<img id="indicatorDocente" src="/sigaa/img/indicator.gif" style="display: none;">
												</td>
											</tr>
										</table>
									</div>
									<div id="membro-externo" >
										<table width="100%">
											<tr>
												<th class="obrigatorio" width="26%">Estagiário:</th>
												<td nowrap="nowrap">
													<h:inputText id="nomeDocenteExt" value="#{ turmaInfantilMBean.docenteTurmaInfantil.docenteExterno.pessoa.nome }" size="70" 
														onfocus="$('categoriaMembro').value=#{categoriaMembro.EXTERNO}" />
													<rich:suggestionbox for="nomeDocenteExt" id="suggestion_docenteExt"  width="430" height="100" minChars="3" 
													    suggestionAction="#{ docenteExterno.autoCompleteNomeDocenteExterno }" var="_docenteExt" fetchValue="#{ _docenteExt.pessoa.nome }" 
													    onsubmit="$('indicatorDocente').style.display='';" oncomplete="$('indicatorDocente').style.display='none';">
												      	
												      	<h:column>
													      	<h:outputText value="#{ _docenteExt.pessoa.nome }"/>
												      	</h:column>
												      	
														<a4j:support event="onselect" reRender="btnAdicionarDocente">
															<f:setPropertyActionListener value="#{ _docenteExt.id }" target="#{ turmaInfantilMBean.docenteTurmaInfantil.docenteExterno.id }" />
														</a4j:support>
													</rich:suggestionbox>
													<img id="indicatorDocente" src="/sigaa/img/indicator.gif" style="display: none;">
												</td>
											</tr>
										</table>
									</div>
								</div>
							</td>
						</tr>
					</table>				
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton value="Adicionar" actionListener="#{ turmaInfantilMBean.adicionarDocenteTurma }" id="btnAdicionarDocente" 
					style="display: #{ (turmaInfantilMBean.docenteTurmaInfantil.docenteExterno.id > 0 || turmaInfantilMBean.docenteTurmaInfantil.docente.id > 0) ? '' : 'none;' }"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<table class="subFormulario" style="width: 100%">
				<tr><td align="center">
					<c:if test="${not empty turmaInfantilMBean.obj.docentesTurmas}">
						<div class="infoAltRem">
						    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Retirar Docente da Turma
						</div>
						<t:dataTable width="100%" var="docenteTurma" value="#{ turmaInfantilMBean.obj.docentesTurmas }"
							styleClass="linhaPar, linhaImpar" id="datatableDocentes" rowIndexVar="indice">
	
							<t:column>
								<f:facet name="header"><f:verbatim>Professor(a)</f:verbatim></f:facet>
								<h:outputText value="#{ docenteTurma.docente.nome }"/>
								<h:outputText value="#{ docenteTurma.docenteExterno.pessoa.nome }"/>
							</t:column>
							
							<t:column>
								<f:facet name="header"><f:verbatim>Tipo</f:verbatim></f:facet>
								<h:outputText value="#{ docenteTurma.docente != null ? 'Docente' : 'Estagiário'}"/>
							</t:column>
	
							<t:column>
								<h:commandLink id="link" actionListener="#{ turmaInfantilMBean.removerDocenteTurma }" >
									<f:param value="#{ indice }" name="indice"/>
									<h:graphicImage url="/img/delete.gif" title="Retirar Docente da Turma" />
								</h:commandLink>
							</t:column>
	
						</t:dataTable>
					</c:if>
				</td></tr>
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
</h:form>

	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>

</f:view>

<script>
var Tabs = {
	    init : function(){
	        var tabs = new YAHOO.ext.TabPanel('tabs-membro');
	        tabs.addTab('membro-docente', "Docente")
	        tabs.addTab('membro-externo', "Estagiário");
	
		    tabs.activate('membro-docente');	////padrão
	
	 		    <c:if test="${sessionScope.aba != null}">
			   	tabs.activate('${sessionScope.aba}');
		    </c:if>
	    }
	}
	YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>