<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.ensino.jsf.DocenteTurmaMBean"%>
<script type="text/javascript">
	JAWR.loader.script('/javascript/prototype-1.6.0.3.js');
</script>
<style>
	.instrutores{width: 600px !important; }
	.colIcone{width: 5% !important;text-align: right;}
	.colCH{width: 15%;text-align: right !important;}
	.colVagas{width: 15%;text-align: right !important;}
	.colPeriodo{width: 20%;text-align: center !important;}
	.direita{text-align:right !important;}
	.tabelaSemBorda{border: 0px !important;margin: 0px !important;}
</style>

<f:view>
	<a4j:keepAlive beanName="grupoAtividadesAP"></a4j:keepAlive>
	<h2 class="title"><ufrn:subSistema /> > Grupos de Atividades > Cadastrar</h2>

	<h:form id="formGrupoAtividadesAP" prependId="false"   enctype="multipart/form-data">

		<div class="descricaoOperacao">
			<p>Caro usuário,</p>
			<p>O formulário abaixo permite efetuar as operações de cadastro e alteração das atividades associadas ao grupo, ou seja:</p>
			<p>Adicionar, alterar ou remover atividades do grupo.</p>
			<p>Adicionar ou remover instrutores da atividade do grupo.</p>
			<p>Caso seja definido um número de vagas, durante inscrição do participante esse valor será verificado.</p>
			<p><strong>Importante:</strong> Os dados somente serão salvos se o botão "Salvar" for pressionado.</p>
		</div>
		
		<center>
				<h:messages/>
				<div class="infoAltRem">
				    <h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>: Adicionar Professor na Atividade
				    <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Atividade do Grupo
				    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Atividade/Remover Professor
		   			<c:if test="${not empty grupoAtividadesAP.atividadeArquivo && grupoAtividadesAP.atividadeArquivo.atividade.idArquivo > 0 }">
					    <img src="/shared/img/icones/download.png" style="overflow: visible;"/>: Baixar Arquivo
					</c:if>   
				</div>
		</center>
		
		<%-- VISUALIZAÇÃO DO GRUPO DE ATIVIDADES --%>
		<table class="formulario" width="99%">
		<caption>Dados do Grupo</caption>
		<tbody>
			<tr>
				<th width="20%" class="rotulo">Grupo:</th>
				<td><h:outputText value="#{grupoAtividadesAP.obj.denominacao}"/></td>
			</tr>	
			<tr>
				<th class="rotulo">Período de Atividades:</th>
				<td>
					<h:outputText value="#{grupoAtividadesAP.obj.inicio}"/>
					a
					<h:outputText value="#{grupoAtividadesAP.obj.fim}"/>
				</td>
			</tr>
			<tr>
				<th class="rotulo">Período de Inscrição:</th>
				<td>
					<h:outputText value="#{grupoAtividadesAP.obj.inicioInscricao}"/>
					a
					<h:outputText value="#{grupoAtividadesAP.obj.fimInscricao}"/>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">		
					Dados da Atividade
				</td>	
			</tr>
			<tr>
				<th  width="20%" class="required">Nome:</th>
				<td>
					<h:inputText id="nome" size="65" maxlength="255" 
						value="#{grupoAtividadesAP.atividadeArquivo.atividade.nome}">
					</h:inputText>
				</td>
			</tr>
				
			<tr>
				<td colspan="2">
					<table class="subFormulario tabelaSemBorda" width="100%">
						<tr>
							<th width="20%">Carga Horária:</th>
							<td width="10%">
								<h:inputText id="cargaHoraria" size="3" maxlength="3"
									onkeyup="return formatarInteiro(this);"
									 
									value="#{grupoAtividadesAP.atividadeArquivo.atividade.ch}">
								</h:inputText>
							</td>
							<th width="10%">Nº de Vagas:</th>
							<td>
								<h:inputText id="numVagas" size="3" maxlength="3"
									onkeyup="return formatarInteiro(this);"
									 
									value="#{grupoAtividadesAP.atividadeArquivo.atividade.numVagas}">
								</h:inputText>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			
			<tr>
				<th class="required">Período:</th>
				<td>
					<t:inputCalendar id="dataInicio" title="Data Inicial" 
						value="#{grupoAtividadesAP.atividadeArquivo.atividade.inicio}" renderAsPopup="true"
						renderPopupButtonAsImage="true" size="10" popupDateFormat="dd/MM/yyyy"
						onkeypress="return formataData(this,event)" maxlength="10" />
						 a  
					<t:inputCalendar 
						id="dataFim" value="#{grupoAtividadesAP.atividadeArquivo.atividade.fim}"  title="Data Final"
						renderAsPopup="true" renderPopupButtonAsImage="true" size="10" popupDateFormat="dd/MM/yyyy"
						onkeypress="return formataData(this,event)" maxlength="10" />
				</td>
			</tr>
			
			<tr>
				<th>Horário:</th>
				<td>
					<t:inputText id="horaInicio" title="Horário Inicial" onkeypress="return(formataHora(this, event))"
						value="#{grupoAtividadesAP.atividadeArquivo.atividade.horarioInicio}" size="5" maxlength="5" />
						 a  
					<t:inputText rendered="#{!grupoAtividadesAP.readOnly}"	id="horaFim" onkeypress="return(formataHora(this, event))"
						value="#{grupoAtividadesAP.atividadeArquivo.atividade.horarioFim}"  title="Horário Final"
				 			size="5" maxlength="5" />
				</td>
			</tr>
						
			<tr>
				<th valign="top">Outras Informações:</th>
				<td>
					<t:inputTextarea id="descricao" title="Descrição" 
						value="#{grupoAtividadesAP.atividadeArquivo.atividade.descricao}" rows="5" cols="80"/>
				</td>
			</tr>
			
			<tr>
				<th valign="top">Ementa:</th>
				<td>
					<t:inputTextarea id="ementa" title="Ementa" 
						value="#{grupoAtividadesAP.atividadeArquivo.atividade.ementa}" rows="5" cols="80"/>
				</td>
			</tr>

			<%-- LISTA DOS INSTRUTORES PARA ATIVIDADE QUE ESTÁ SENDO CADATRADA --%>
			<tr>
				<th width="20%">Professores:</th>
				<td align="left">
						
						<h:inputHidden value="#{docenteTurmaBean.tipoBuscaDocente}" id="idTipoBusca"/>
						<div id="abas-docentesTurma">
							<div id="docentesTurma"  class="aba">
								<table width="100%">
									<tr>
										<th class="required">Docente:</th>
										<td>
											 <h:inputText value="#{grupoAtividadesAP.pessoa.nome}" id="pessoa" size="65"/>
											 <rich:suggestionbox id="suggestion"  width="430" height="100" minChars="3" 
												 	 frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200" 
												 	for="pessoa" suggestionAction="#{servidorAutoCompleteMBean.autocompleteNomeServidor}" 
												 	var="_servidor" fetchValue="#{_servidor.pessoa.nome}">
												<h:column>
													<h:outputText value="#{_servidor.siape} - #{_servidor.pessoa.nome} (#{_servidor.unidade.nome})"/>
												</h:column>
												
										        <a4j:support  event="onselect" focus="pessoa" reRender="pessoa" 
										        	actionListener="#{grupoAtividadesAP.carregaPessoa}" >
													<f:attribute name="idServidor" value="#{_servidor.id}"  />
												</a4j:support>
												
											  </rich:suggestionbox>
											
											<h:commandButton image="#{ctx}/img/adicionar.gif"  id="btnAddInstrutor" title="Adicionar Professor na Atividade"
												actionListener="#{grupoAtividadesAP.addInstrutor}"/>		
										</td>
									</tr>
								</table>
							</div>
						</div>
						<div id="docentesTurmaExternos" class="aba">
								<table width="100%">
									<tr>
										<th class="required">Docente Externo:</th>
										<td>
											 <h:inputText value="#{grupoAtividadesAP.docExterno.pessoa.nome}" id="pessoaExt" size="65"/>
											 <rich:suggestionbox id="nomeDocenteExt"  width="430" height="100" minChars="3" 
												 	 frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200" 
												 	for="pessoaExt" suggestionAction="#{docenteExterno.autoCompleteNomeDocenteExterno}" 
												 	var="_docenteExt" fetchValue="#{_docenteExt.pessoa.nome}">
												<h:column>
													<h:outputText value="#{_docenteExt.pessoa.nome} (#{_docenteExt.unidade.nome})"/>
												</h:column>
												
										        <a4j:support  event="onselect" focus="pessoaExt" reRender="pessoaExt" 
										        	actionListener="#{grupoAtividadesAP.carregaDocenteExterno}" >
													<f:attribute name="idDocenteExt" value="#{_docenteExt.id}"  />
												</a4j:support>
												
											  </rich:suggestionbox>
											
											<h:commandButton image="#{ctx}/img/adicionar.gif"  id="btnAddInstrutorExt" title="Adicionar Professor na Atividade"
												actionListener="#{grupoAtividadesAP.addInstrutorExterno}"/>		
										</td>
									</tr>
								</table>
							</div>
						</td>
				</tr>
				<tr>
					<th width="20%"></th>
					<td align="left">
						<t:dataTable value="#{grupoAtividadesAP.atividadeArquivo.atividade.instrutores}" 
							rendered="#{not empty grupoAtividadesAP.atividadeArquivo.atividade.instrutores}"
						 	var="_ia" styleClass="subListagem" columnClasses="colNome,colIcone" 
						 	rowClasses="linhaPar, linhaImpar">
								<t:column styleClass="colNome" headerstyleClass="colNome">
									<f:facet name="header">
										<f:verbatim>Nome</f:verbatim>
									</f:facet>
									<h:outputText value="#{_ia.pessoa.nome}"/>
								</t:column>
			
								<t:column styleClass="colIcone" headerstyleClass="colIcone">
									<f:facet name="header">
										<f:verbatim>&nbsp;</f:verbatim>
									</f:facet>
						
									<h:commandLink styleClass="noborder" title="Remover Professor" id="removerInstrutor"
										onclick="#{confirmDelete}" immediate="true"	actionListener="#{grupoAtividadesAP.removeInstrutor}" >
												<h:graphicImage url="#{ctx}/img/delete.gif" />
												<f:attribute name="pessoa" value="#{_ia.pessoa.id}"/>
									</h:commandLink>
								</t:column>		
					</t:dataTable>
				</td>
			</tr>

			<tr>
				<th>Anexo:</th>
				<td>
					<t:inputFileUpload id="arquivo" title="Arquivo" size="50" maxlength="255" 
						value="#{grupoAtividadesAP.atividadeArquivo.arquivo}"/>
				</td>
			</tr>
			
			<c:if test="${grupoAtividadesAP.atividadeArquivo.atividade.idArquivo > 0 }">
			<tr>
				<th></th>
				<td>
					<a href="/sigaa/verProducao?idProducao=${grupoAtividadesAP.atividadeArquivo.atividade.idArquivo}&key=${ sf:generateArquivoKey(grupoAtividadesAP.atividadeArquivo.atividade.idArquivo) }" title="Baixar Arquivo" target="_blank">
						 <img src="/shared/img/icones/download.png"/> Baixar Arquivo
					</a>
				</td>
			</tr>
			<tr>
				<td colspan="2" height="10px"></td>
			</tr>
			</c:if>
			
			<tr>
				<th></th>
				<td>
					<h:commandButton value="#{grupoAtividadesAP.confirmButtonAtividade}" 
							actionListener="#{grupoAtividadesAP.addAtividade}" id="btnAddUpdAtividade"/> 
				</td>
			</tr>
						
			<%-- LISTA DAS ATIVIDADES PARA O GRUPO --%>
			<tr>
				<td colspan="2" height="10px"></td>
			</tr>
			<tr>
				<td colspan="2">	
					<table class="subListagem" width="90%">
						<caption>Atividades do Grupo</caption>
					</table>
					
					<t:dataTable value="#{grupoAtividadesAP.listaAtividadesAnexosAtivos}" rendered="#{not empty grupoAtividadesAP.listaAtividadesAnexosAtivos}"
						 var="_reg" styleClass="subListagem"  width="90%" columnClasses="colNome,colPeriodo,colCH,colVagas,colIcone"
						  rowClasses="linhaPar, linhaImpar">
								<t:column styleClass="colNome" headerstyleClass="colNome">
									<f:facet name="header">
										<f:verbatim>Nome</f:verbatim>
									</f:facet>
									<h:outputText value="#{_reg.atividade.nome}"/>
								</t:column>
								
								<t:column styleClass="colPeriodo" headerstyleClass="colPeriodo">
									<f:facet name="header">
										<f:verbatim>Horário</f:verbatim>
									</f:facet>
									<h:outputText value="#{_reg.atividade.horarioInicio} a #{_reg.atividade.horarioFim}" rendered="#{not empty _reg.atividade.horarioInicio}"/>
									<h:outputText value="Não informado" rendered="#{empty _reg.atividade.horarioInicio}"/>
								</t:column>
			
								<t:column styleClass="colPeriodo" headerstyleClass="colPeriodo">
									<f:facet name="header">
										<f:verbatim>Período</f:verbatim>
									</f:facet>
									<h:outputText value="#{_reg.atividade.descricaoPeriodo}"/>
								</t:column>
								
								<t:column styleClass="colCH" headerstyleClass="colCH">
									<f:facet name="header">
										<f:verbatim>Carga Horária</f:verbatim>
									</f:facet>
									<h:outputText value="#{_reg.atividade.ch} h" rendered="#{not empty _reg.atividade.ch}"/> 
									<h:outputText value="Não informado" rendered="#{empty _reg.atividade.ch}"/>
								</t:column>
								
								<t:column styleClass="colVagas" headerstyleClass="colVagas">
									<f:facet name="header">
										<f:verbatim>Nº de Vagas</f:verbatim>
									</f:facet>
									<h:outputText value="#{_reg.atividade.numVagas}" rendered="#{not empty _reg.atividade.numVagas}"/> 
									<h:outputText value="Não informado" rendered="#{empty _reg.atividade.numVagas}"/>
								</t:column>
				
								<t:column styleClass="colIcone" headerstyleClass="colIcone">
									<f:facet name="header">
										<f:verbatim>&nbsp;</f:verbatim>
									</f:facet>
						
									<h:commandLink styleClass="noborder" title="Alterar Atividade do Grupo" id="atualizarRegistro"
										rendered="#{grupoAtividadesAP.enableAlterarRemoverAtividade}"
										actionListener="#{grupoAtividadesAP.atualizaAtividade}" >
										<h:graphicImage url="/img/alterar.gif" />
										<f:attribute name="mapa" value="#{_reg}"/>
									</h:commandLink>
									<h:commandLink  styleClass="noborder" title="Remover Atividade" id="removerAtividade"
										rendered="#{grupoAtividadesAP.enableAlterarRemoverAtividade}"
										onclick="#{confirmDelete}" actionListener="#{grupoAtividadesAP.removeAtividade}" >
										<h:graphicImage url="/img/delete.gif" />
										<f:attribute name="mapa" value="#{_reg}"/>
									</h:commandLink>
								</t:column>		
					</t:dataTable>
		
						
				</td>
			</tr>
					
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Salvar" action="#{grupoAtividadesAP.cadastrar}" id="btnCadastrarAtividade"/> 
						<h:commandButton value="<< Voltar" action="#{grupoAtividadesAP.formGrupo}" immediate="true"  id="btnFormGrupoAtividade"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{grupoAtividadesAP.cancelar}" immediate="true" id="btncancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>
	
	<br>
	
	<c:if test="${!grupoAtividadesAP.readOnly}">
	<center>
		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
	</center>
	</c:if>
</f:view>

<script type="text/javascript">
		var Abas = function() {
		
			var setTipoBusca = function(e, aba) {
				var idAba = aba.id;
				var categoria = getEl('formGrupoAtividadesAP:idTipoBusca');
				switch(idAba) {
					case 'membrosPrograma': categoria.dom.value = <%= DocenteTurmaMBean.DOCENTE_PROGRAMA %>; break;
					case 'externoPrograma': categoria.dom.value = <%= DocenteTurmaMBean.DOCENTE_EXTERNO_PROGRAMA %>; break;
					case 'docentesExternos':
					case 'docentesTurmaExternos': categoria.dom.value = <%= DocenteTurmaMBean.DOCENTES_EXTERNOS %>; break;
					case 'docentesTurma': categoria.dom.value = <%= DocenteTurmaMBean.DOCENTES_TURMA %>; break;
				}
			};
			return {
			    init : function(){
				        var abas = new YAHOO.ext.TabPanel('abas-docentesTurma');
						abas.on('tabchange', setTipoBusca);
						abas.addTab('docentesTurma', "Docentes");
						abas.addTab('docentesTurmaExternos', "Docentes Externos");
						switch( getEl('formDocentes:idTipoBusca').dom.value ) {
							case ''+<%=1%>:  abas.activate('docentesTurma'); break;
							case ''+<%=2%>:  abas.activate('docentesTurmaExternos'); break;
							default: abas.activate('docentesTurma'); break;
						}

			    }
		    }
		}();
		YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
</script>		
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
