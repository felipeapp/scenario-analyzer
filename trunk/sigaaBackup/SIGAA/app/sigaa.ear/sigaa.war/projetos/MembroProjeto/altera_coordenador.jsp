<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Alterar Coordenador</h2>
	
	<h:form id="equipe">
	
		<input type="hidden" name="categoriaMembro" id="categoriaMembro" value="${categoriaAtual}"/>
		<input type="hidden" name="lista_origem" id="lista_origem" value="${listaOrigem}"/>
		
		<div class="descricaoOperacao">
			<b>Caro Coordenador,</b>
			<br />
			A data da troca do coordenador será a data de início do novo coordenador. Neste caso, o coordenador atual será finalizado com a data final sendo a 
			data de início do novo coordenador. O coordenador vigente será o novo coordenador e o senhor perderá os privilégios de coordenador desta ação.
		</div>
		
		<table class="formulario" width="100%">
			<caption class="listagem">Dados do Coordenador Atual</caption>
			<tr>
				<th>Projeto:</th>
				<td><b><h:outputText value="#{membroProjeto.projeto.anoTitulo}" /></b></td>
			</tr>
			<tr>
				<th>Nome:</th>
				<td><b><h:outputText value="#{membroProjeto.membroEquipeAux.pessoa.nome}" rendered="#{not empty membroProjeto.membroEquipe}" /></b></td>
			</tr>
	
			<tr>
				<th>Categoria:</th>
				<td><b><h:outputText value="#{membroProjeto.membroEquipeAux.categoriaMembro.descricao}"/></b></td>
			</tr>
			
			<tr>
				<th>Função:</th>
				<td><b><h:outputText value="#{membroProjeto.membroEquipeAux.funcaoMembro.descricao}" /></b></td>
			</tr>
			
			<tr>
				<th>Período:</th>
				<td>
					<b>
					<fmt:formatDate value="${membroProjeto.membroEquipeAux.dataInicio}" pattern="dd/MM/yyyy"/> a 
						<fmt:formatDate value="${membroProjeto.membroEquipeAux.dataFim}" pattern="dd/MM/yyyy"/>
					</b>
				</td>
			</tr>			
		</table>
		
		<br />
		<br />
		
		<table class="formulario" width="100%">
			<caption class="listagem">Dados do novo Coordenador</caption>
			<tr>
				<td colspan="2">
					<div id="tabs-membro">
							<div id="membro-docente">
								<table width="100%">
									<tr>
										<th width="15%"  class="required">Docente:</th>
										<td>
											<h:inputText value="#{membroProjeto.docente.pessoa.nome}" id="docente" size="65" onfocus="$('categoriaMembro').value=#{categoriaMembro.DOCENTE}"/>
											<rich:suggestionbox id="suggestion_docente"  width="430" height="100" minChars="3" 
													frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200" 
												    for="docente" suggestionAction="#{membroProjeto.autoCompleteNomeDocente}" 
												    var="_docente" fetchValue="#{_docente.siapeNome}"
												    onsubmit="$('indicatorDocente').style.display='';" 
											      	oncomplete="$('indicatorDocente').style.display='none';" 
											      	reRender="indicatorDocente">
												  <f:param name="apenasAtivos" value="true" />
										    	  <f:param name="apenasDocentes" value="true" />
											      <h:column>
												      <h:outputText value="#{_docente.siapeNome}"/>
											      </h:column>
											      <a4j:support event="onselect" focus="docente" 
												      actionListener="#{membroProjeto.carregaDocente}" >
												      <f:attribute name="docenteAutoComplete" value="#{_docente}"/>
											      </a4j:support>
											</rich:suggestionbox>
											<img id="indicatorDocente" src="/sigaa/img/indicator.gif" style="display: none;">
											<ufrn:help img="/img/ajuda.gif">Apenas os docentes do Quadro Permanente da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
										</td>
									</tr>
									
									<tr>
										<th width="15%"  class="required">Função:</th>
										<td>
											<b><h:outputText id="funcaoMembroEquipeDocente" value="#{membroProjeto.membroEquipe.funcaoMembro.descricao}" > 
												<f:param name="idFuncaoMembro" value="#{membroProjeto.membroEquipe.funcaoMembro.id}" />
											</h:outputText></b>
										</td>
									</tr>										
								</table>
							</div>
							
							<div id="membro-servidor">
								<table width="100%">
									<tr>
										<th width="15%" class="required">Servidor:</th>
										<td>
											<h:inputText id="servidor"	value="#{membroProjeto.servidor.pessoa.nome}" size="80" onfocus="$('categoriaMembro').value=#{categoriaMembro.SERVIDOR}"/>
											<rich:suggestionbox id="suggestion_servidor"  width="430" height="100" minChars="3" 
												frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200" 
											      for="servidor" suggestionAction="#{membroProjeto.autoCompleteNomeServidorTecnico}" 
											      var="_servidor" fetchValue="#{_servidor.siapeNome}"
											      onsubmit="$('indicatorServidor').style.display='';" 
											      oncomplete="$('indicatorServidor').style.display='none';" 
											      reRender="indicatorServidor">
												<h:column>
													<h:outputText value="#{_servidor.siapeNome}" />
												</h:column>
												<a4j:support event="onselect" focus="servidor" 
											      	actionListener="#{membroProjeto.carregaServidorTecnico}" >
											      	<f:attribute name="servidorAutoComplete" value="#{_servidor}"/>
										     	</a4j:support>
											</rich:suggestionbox>
											<img id="indicatorServidor" src="/sigaa/img/indicator.gif" style="display: none;">
											<ufrn:help img="/img/ajuda.gif">Apenas os servidores do Quadro Permanente da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
										</td>
									</tr>
									
									<tr>
										<th width="15%"  class="required">Função:</th>
										<td>
											<b><h:outputText id="funcaoMembroEquipeServidor" value="#{membroProjeto.membroEquipe.funcaoMembro.descricao}" > 
												<f:param name="idFuncaoMembro" value="#{membroProjeto.membroEquipe.funcaoMembro.id}" />
											</h:outputText></b>
										</td>
									</tr>									
								</table>
							</div>
					</div>
				</td>
			</tr>
			
			<tr>
				<th width="15%" align="left"  class="required">Remuneração / Bolsa:</th>
					<td>
						<table>
							<tr>
								<td>
									<h:selectOneRadio value="#{membroProjeto.membroEquipe.remunerado}" id="membroRemunerado">
										<f:selectItem itemValue="true" itemLabel="Sim"/>
										<f:selectItem itemValue="false" itemLabel="Não"/>
									</h:selectOneRadio>
								</td>
								<td>
									<ufrn:help img="/img/ajuda.gif">Marque SIM caso o membro da equipe possua bolsa ou outro tipo de remuneração.</ufrn:help>
								</td>
							</tr>
						</table>
					</td>
			</tr>
	
				<tr>
					<th  class="required">CH Semanal:</th>
					<td>
						<h:inputText id="chSemanalDedicada" value="#{membroProjeto.membroEquipe.chDedicada}" maxlength="3" size="5" onkeyup="formatarInteiro(this)" /> horas
					</td>
				</tr>
				
				<tr>
					<th class="required">Data Início:</th>
					<td>
						<t:inputCalendar  id="inicio" value="#{membroProjeto.membroEquipe.dataInicio}" 
							renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
							size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" >
							<f:converter converterId="convertData" />
						</t:inputCalendar>					
					
					</td>
				</tr>
				
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton id="btnAlterarCoord" value="Alterar Coordenador" action="#{membroProjeto.alterarCoordenador}" />
							<h:commandButton id="btn_cancelar" value="Cancelar" action="#{membroProjeto.cancelar}" onclick="#{confirm}"/>
						</td>
					</tr>
				</tfoot>
		</table>
		<br />
		<center>
			<div class="required-items">
				<span class="required"></span>Campos de Preenchimento Obrigatório
			</div>
		</center>
		
	</h:form>
</f:view>


<script type="text/javascript">
	var Tabs = {
	    init : function(){
	        var tabs = new YAHOO.ext.TabPanel('tabs-membro');
	        tabs.addTab('membro-docente', "Docente")
	        tabs.addTab('membro-servidor', "Servidor Técnico-Administrativo");

   		        tabs.activate('membro-docente');	////padrão

   		      <c:if test="${sessionScope.aba != null}">
					tabs.activate('${sessionScope.aba}');
			  </c:if>

	    }
	}
	YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);
	
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>