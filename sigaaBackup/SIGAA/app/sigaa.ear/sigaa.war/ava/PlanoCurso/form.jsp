<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>

<f:view>

	<a4j:keepAlive beanName="planoCurso" />
	
	<p:resources />
	<link rel="stylesheet" type="text/css" href="/sigaa/ava/primefaces/redmond/skin.css" />
	
	<style>
		.acao { text-align:center; width:20px; }
		.data { width:85px; }
		.hora { width:110px; }
		.descricao { overflow : hidden; }
		.conteudo { width:300px; }
		
		.divLinha {
			clear:both;
			text-align:left;
			margin-top:5px;
		}
		
		.divLinha .labelLinha{
			float:left;
			display:block;
			width:180px;
			margin-right:4px;
			text-align:right;
			margin-bottom:10px;
		}
		
		.subformulario {
			margin-left:auto;
			margin-right:auto;
		}
		
		.listaVazia {
			text-align:center;
			font-weight:bold;
			color:#FF0000;
			margin-top:10px;
		}
			
		.adInfo p {
				text-align:justify;
				margin-bottom:10px;
				padding:10px;
				background-color: #EFF3FA;
				width:60%;
				border: 1px solid #CCDDEE;
		}
		
		table.listing { border: 1px solid #666666; margin: 10px auto; margin-top: 0; width: 95%; border-collapse: collapse; }
		table.listing thead tr th { font-size: 1em; background-color: #DCDCDC; font-weight: bold; text-align: center; padding: 2px; }
		table.listing tbody tr td { font-size: 1em; padding: 3px 8px; border-left: 1px solid #D9D9D9; }
		table.listing tbody tr td.first { border-left: 0; }
	</style>
	

	<h2><ufrn:subSistema /> &gt; Gerenciar Plano de Curso</h2>
	
	<a4j:region>
		<h:form id="form">

			<a4j:poll interval="#{5*60*1000}" action="#{ planoCurso.salvarPoll }" onsubmit="exibeAvisoSalvar();" oncomplete="escondeAvisoSalvar();" />
			
			<div class="descricaoOperacao">
				<strong>Caro(a) professor(a)</strong>,
				<p>De acordo com a resolução nº 227/2009-CONSEPE, de 03 de Dezembro de 2009, Art 42, é necessário que o plano de curso de todas as turmas seja preenchido no início do semestre.</p>
				<p>Por favor, preencha o formulário abaixo para obter acesso à esta turma virtual.</p>
				<p>É possível salvar o formulário para continuar em outro momento. Para isso, clique no botão "Salvar". Ao concluir a inserção das informações do plano de curso, clique em "Salvar e Enviar" para obter acesso à turma virtual.</p>
				<p><strong>Os dados informados neste formulário são uma previsão do que será passado à turma e podem ser alterados no decorrer do semestre.</strong></p>
				<p>O formulário é salvo automaticamente a cada cinco minutos.</p>
			</div>

			<div class="adInfo" align="center">
				<p>Caso já tenha lecionado em outra turma desta mesma disciplina é possível 
				<b>Importar o Plano de Curso, as Aulas e as Referências</b> para esta turma. Para realizar a importação, clique 
				<h:commandLink value="aqui" action="#{planoCurso.iniciarImportacaoDados}" />.</p>
			</div>
			
			<table class="formulario" width="100%">
				<caption>Dados do Plano</caption>
				
				<%-- Dados da Turma --%>
				<tr>
					<td>
						<table class="subformulario" style="width: 100%;">
							<caption>Dados da Turma</caption>
							<tr>
								<th style="width:200px;"><strong>Turma:</strong></th>
								<td>${planoCurso.obj.turma.descricaoSemDocente}</td>
							</tr>
							<tr>
								<th><strong>Créditos:</strong></th>
								<td>
									${planoCurso.obj.turma.disciplina.detalhes.crTotal}
								</td>
							</tr>
							<tr>
								<th><strong>Horário:</strong></th>
								<td>
									${planoCurso.obj.turma.descricaoHorario}
								</td>
							</tr>
						</table>
					</td>
				</tr>
				
				<%-- Dados digitados. --%>
				<tr>
					<td>
						<table class="subformulario" style="width:100%;margin-bottom:20px;">
							<caption>Metodologia de Ensino e Avaliação</caption>
							<tr>
								<th style="vertical-align:top;">Metodologia:<span class="obrigatorio">&nbsp;</span></th>
								<td><h:inputTextarea id="metodologia" value="#{planoCurso.obj.metodologia}" style="width:645px;height:100px;" /></td>
							</tr>
							<tr>
								<th style="vertical-align:top;">Procedimentos de Avaliação da Aprendizagem:<span class="obrigatorio">&nbsp;</span></th>
								<td><h:inputTextarea id="conteudo" value="#{planoCurso.obj.procedimentoAvalicao}" style="width:645px;height:100px;" /></td>
							</tr>
							<tr>
								<th style="vertical-align:top;">Horário de atendimento:</th>
								<td><h:inputText id="horarioAtendimento" value="#{planoCurso.obj.horarioAtendimento}" style="width:645px;" /></td>
							</tr>
						</table>
					</td>
				</tr>
				
				<%-- Listagem de Tópicos de Aulas --%>
				<tr>
					<td>
						<table class="subformulario" style="width:97%;margin:auto;margin-bottom:20px;border:1px solid #DDDDDD;">
							<caption>Cronograma de Aulas</caption>
							<tr><td>
							
								<div class="descricaoOperacao">
									De acordo com o Item IV do artigo supracitado, o cronograma de aulas deve ser informado.<br/>
									O formulário abaixo permite descrever o que será ministrado em cada aula.
								</div>
																
								<a4j:outputPanel id="formTA">
								
									<a4j:outputPanel rendered="#{planoCurso.turma.ead}">
									
										<h:inputHidden id="idItemEad"/>
										
										<table class="subformulario" style="width:80%;">
										<caption>Aulas de Ensino à Distância</caption>
										<tbody>
											<tr>
												<td colspan="2">
													<div class="divLinha">
														<label class="labelLinha">Data Inicial:<span class="required">&nbsp;</span></label>
														<h:selectOneMenu value="#{planoCurso.inicioTA}" id="inicioTAEad" onchange="atualizaData(true,false,false,true);">			    
															<f:selectItems value="#{planoCurso.aulasCombo}" />
														</h:selectOneMenu>
													</div>
												</td>
											</tr>
											<tr>
												<td colspan="2">
													<div class="divLinha">
														<label class="labelLinha">Data Final:<span class="required">&nbsp;</span></label>
														<h:selectOneMenu value="#{planoCurso.fimTA}" id="fimTAEad" onchange="atualizaData(false,false,false,true);">
															<f:selectItems value="#{planoCurso.aulasCombo}" />
														</h:selectOneMenu>
													</div>
												</td>
											</tr>
											<tr><td colspan="2">
												<c:if test="${not empty planoCurso.topicosEad}">
													<table class="listing" style="width:80%;">
														<thead>
															<tr>
																<th></th>
																<th>Aula</th>
																<th>Descrição</th>
															</tr>
														</thead>
											
														<tbody>
															<c:forEach var="t" items="#{ planoCurso.topicosEad }" varStatus="loop">
																<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
																	<td class="first" style="width:5%;text-align:center"">
																		<input type="radio" name="id" id="id" value="${t.id}" class="noborder" onclick="atualizaConteudo(this);" />
																	</td>
																	<td style="width:5%;text-align:center"><h:outputText id="aulaEad" value="#{ t.aula }"/></td>
																	<td class="conteudo"><h:outputText id="conteudoEad" value="#{ t.conteudo }"/></td>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</c:if>	
											</td></tr>
										</tbody>
										<tfoot>
											<tr><td colspan="2">
												<a4j:commandButton id="adicionarTAEad" value="Adicionar Tópico" actionListener="#{planoCurso.adicionarTopicoAula}" reRender="panelTA,listaTopicosAula" />
											</td></tr>
										</tfoot>
										</table>										
									</a4j:outputPanel>
									
									<a4j:outputPanel rendered="#{!planoCurso.turma.ead}">	
										<table class="subformulario" style="width:80%;">
											<caption>Aulas</caption>
											<tr><td colspan="2">
											
												<a4j:outputPanel rendered="#{!planoCurso.alterarTopico}">
													<div class="divLinha">
														<label class="labelLinha">Data Inicial:<span class="required">&nbsp;</span></label>
														<h:selectOneMenu value="#{planoCurso.inicioTA}" id="inicioTA" onchange="atualizaData(true,true,false,false);">			    
															<f:selectItems value="#{planoCurso.aulasCombo}" />
														</h:selectOneMenu>
													</div>
													<div class="divLinha">
														<label class="labelLinha">Data Final:<span class="required">&nbsp;</span></label>
														<h:selectOneMenu value="#{planoCurso.fimTA}" id="fimTA" onchange="atualizaData(false,true,false,false);">
															<f:selectItems value="#{planoCurso.aulasCombo}" />
														</h:selectOneMenu>
													</div>
												</a4j:outputPanel>
												
												<a4j:outputPanel rendered="#{planoCurso.alterarTopico}">
													<div class="divLinha">
														<label class="labelLinha">Data Inicial:<span class="required">&nbsp;</span></label>
														<h:selectOneMenu value="#{planoCurso.inicioTA}" id="inicioTAAlterar" onchange="atualizaData(true,false,true,false);">			    
															<f:selectItems value="#{planoCurso.inicioTACombo}" />
														</h:selectOneMenu>
													</div>
													<div class="divLinha">
														<label class="labelLinha">Data Final:<span class="required">&nbsp;</span></label>
														<h:selectOneMenu value="#{planoCurso.fimTA}" id="fimTAAlterar" onchange="atualizaData(false,false,true,false);">
															<f:selectItems value="#{planoCurso.fimTACombo}" />
														</h:selectOneMenu>
													</div>
												</a4j:outputPanel>
												
												<div class="divLinha"><label class="labelLinha">Descrição:<span class="required">&nbsp;</span></label><h:inputText id="descricaoTA" value="#{planoCurso.topicoAula.descricao}" style="width:550px;" maxlength="200"/></div>
												<div class="divLinha"><label class="labelLinha">Conteúdo:</label><h:inputTextarea id="conteudoTA" value="#{planoCurso.topicoAula.conteudo}" style="width:550px;height:50px;"/></div>
											</td></tr>
											<tfoot>
												<tr><td colspan="2">
													<a4j:commandButton id="adicionarTA" value="Adicionar Tópico" actionListener="#{planoCurso.adicionarTopicoAula}" rendered="#{!planoCurso.alterarTopico}" reRender="formTA,panelTA,listaTopicosAula" />
													<a4j:commandButton id="alterarTA" value="Alterar Tópico" actionListener="#{planoCurso.alterarTopicoAula}" rendered="#{planoCurso.alterarTopico}" reRender="formTA,panelTA,listaTopicosAula" />
													<a4j:commandButton  actionListener="#{planoCurso.limparTopico}" oncomplete="zeraTA();" value="Limpar" reRender="formTA"/>
												</td></tr>
											</tfoot>
										</table>
									</a4j:outputPanel>	
								</a4j:outputPanel>
									
								<a4j:outputPanel id="panelTA">
									<h:outputText value="#{planoCurso.mensagemSucessoTopicoAula}" style="color:#00AA00;margin:10px;text-align:center;display:block;font-weight:bold;" escape="false" />
									<h:outputText value="#{planoCurso.mensagemErroTopicoAula}" style="color:#AA0000;margin:10px;text-align:center;display:block;font-weight:bold;" escape="false" />
								
									<c:if test="${empty planoCurso.topicosAula}">
										<div class="listaVazia">Nenhuma aula cadastrada.</div>
									</c:if>
									<c:if test="${not empty planoCurso.topicosAula}">
										<div class="infoAltRem" style="width:100%">
											<c:if test="${!planoCurso.turma.ead}">
												<h:graphicImage value="/ava/img/page_edit.png" />: Alterar Tópico de Aula
											</c:if>	
											<h:graphicImage value="/img/delete.gif" />: Remover Tópico de Aula
										</div>
							
										<t:dataTable id="listaTopicosAula" value="#{planoCurso.topicosAula}" var="t" columnClasses="data,data,descricao,acao" rowClasses="linhaPar,linhaImpar" rowIndexVar="indice" style="width:100%;" >
											<t:column>
												<f:facet name="header">
													<f:verbatim>
														<p align="center"><h:outputText value="Início"/></p>
													</f:verbatim>	
												</f:facet>
												<div style="text-align:center;"><h:outputText value="#{t.data }" /></div>
											</t:column>
											
											<t:column>
												<f:facet name="header">
													<f:verbatim>
														<p align="center"><h:outputText value="Fim"/></p>
													</f:verbatim>		
												</f:facet>
												<div style="text-align:center;"><h:outputText value="#{t.fim }" /></div>
											</t:column>
											
											<t:column>
												<f:facet name="header"><h:outputText value="Descrição"/></f:facet>
												<div style="width:725px;overflow:hidden;height:15px;"><h:outputText value="#{t.descricao }" /></div>
											</t:column>
											
											<t:column>
												<a4j:commandLink actionListener="#{planoCurso.preEditarTopicoAula}" rendered="#{!planoCurso.turma.ead}" oncomplete="alterarTA();" title="Alterar Tópico de Aula" reRender="formTA,panelTA,listaTopicosAula">
													<f:param name="indiceTA" value="#{indice}" />
													<h:graphicImage value="/ava/img/page_edit.png" />
												</a4j:commandLink>
											</t:column>
											
											<t:column>
												<a4j:commandLink actionListener="#{planoCurso.removerTopicoAula}" title="Remover Tópico de Aula" onclick="if (!confirm('Confirma a remoção desta aula?')) return false;" reRender="panelTA,listaTopicosAula">
													<f:param name="indiceTA" value="#{indice}" />
													<h:graphicImage value="/img/delete.gif" />
												</a4j:commandLink>
											</t:column>
											
										</t:dataTable>
									</c:if>
								</a4j:outputPanel>
							</td></tr>
						</table>
					</td>
				</tr>
					
				<%-- Listagem de Avaliações --%>
				<tr>
					<td>
						<table class="subformulario" style="width:97%;margin:auto;margin-bottom:20px;border:1px solid #DDDDDD;">
							<caption>Avaliações</caption>
							<tr><td>
							
								<div class="descricaoOperacao">
									De acordo com o Item IV, as avaliações também devem ser informadas.
								</div>
								
								<a4j:outputPanel id="formAV">
								<table class="subformulario" style="width:80%;">
									<caption>Avaliações</caption>
									<tr><td colspan="2">
									
										<div class="divLinha">
											<label class="labelLinha">Descrição:<span class="required">&nbsp;</span></label>
											<h:selectOneMenu id="descricaoAV" value="#{planoCurso.avaliacao.descricao}">
												<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
												<f:selectItems value="#{planoCurso.avaliacoesCombo}" />
											</h:selectOneMenu>
										</div>
										
										<div class="divLinha">
											<label class="labelLinha">Data:<span class="required">&nbsp;</span></label>
											<t:inputCalendar id="dataAV" value="#{planoCurso.avaliacao.data}" renderAsPopup="true" popupDateFormat="dd/MM/yyyy" onkeypress="return formataData(this,event)" renderPopupButtonAsImage="true" size="10" maxlength="10" />
										</div>
										
										<div class="divLinha">
											<label class="labelLinha">Hora:<span class="required">&nbsp;</span></label>
											<t:inputText id="horaAV" value="#{planoCurso.avaliacao.hora}" size="25" maxlength="30" />
										</div>
									</td></tr>
									<tfoot>
										<tr><td colspan="2">
											<a4j:commandButton id="adicionarAV" value="Adicionar Avaliação" actionListener="#{planoCurso.adicionarAvaliacao}" rendered="#{!planoCurso.alterarAvaliacao}" reRender="formAV,panelAV,listaAvaliacoes" />
											<a4j:commandButton id="alterarAV" value="Alterar Avaliação" actionListener="#{planoCurso.alterarDataAvaliacao}" rendered="#{planoCurso.alterarAvaliacao}" reRender="formAV,panelAV,listaAvaliacoes" />
											<a4j:commandButton  actionListener="#{planoCurso.limparAvaliacao}" oncomplete="zeraAV();" value="Limpar" reRender="formAV"/>
										</td></tr>
									</tfoot>
								</table>
								</a4j:outputPanel>
							
								<a4j:outputPanel id="panelAV">
									<h:outputText value="#{planoCurso.mensagemSucessoAvaliacao}" style="color:#00AA00;margin:10px;text-align:center;display:block;font-weight:bold;" escape="false" />
									<h:outputText value="#{planoCurso.mensagemErroAvaliacao}" style="color:#AA0000;margin:10px;text-align:center;display:block;font-weight:bold;" escape="false" />
								
									<c:if test="${empty planoCurso.avaliacoes}">
										<div class="listaVazia">Nenhuma avaliação cadastrada.</div>
									</c:if>
									
									<c:if test="${not empty planoCurso.avaliacoes}">
										<div class="infoAltRem">
											<h:graphicImage value="/ava/img/page_edit.png" />: Alterar Avaliação
											<h:graphicImage value="/img/delete.gif" />: Remover Avaliação
										</div>
									
										<t:dataTable id="listaAvaliacoes" value="#{planoCurso.avaliacoes}" var="av" columnClasses="data,data,descricao,acao" rowClasses="linhaPar,linhaImpar" rowIndexVar="indice" style="width:100%;">
											<t:column>
												<f:facet name="header"><h:outputText value="Data"/></f:facet>
												<h:outputText value="#{av.data }" />
											</t:column>
											
											<t:column>
												<f:facet name="header"><h:outputText value="Hora"/></f:facet>
												<h:outputText value="#{av.hora }" style="overflow:hidden;" />
											</t:column>
											
											<t:column>
												<f:facet name="header"><h:outputText value="Descrição"/></f:facet>
												<h:outputText value="#{av.descricao }" style="overflow:hidden;" />
											</t:column>
											
											<t:column>
												<a4j:commandLink actionListener="#{planoCurso.preEditarAvaliacao}" oncomplete="alterarAV()" title="Alterar Avaliação" reRender="formAV,panelAV,listaAvaliacoes">
													<f:param name="indiceAV" value="#{indice}" />
													<h:graphicImage value="/ava/img/page_edit.png" />
												</a4j:commandLink>
											</t:column>
											
											<t:column>
												<a4j:commandLink actionListener="#{planoCurso.removerAvaliacao}" title="Remover Avaliação" onclick="if (!confirm('Confirma a remoção desta avaliação?')) return false;" reRender="panelAV,listaAvaliacoes">
													<f:param name="indiceAV" value="#{indice}" />
													<h:graphicImage value="/img/delete.gif" />
												</a4j:commandLink>
											</t:column>
											
										</t:dataTable>
									</c:if>
								</a4j:outputPanel>
							</td></tr>
						</table>
					</td>
				</tr>
	
				<%-- Listagem de Indicações de referências --%>
				<tr>
					<td>
						<a id="indicacaoReferencia" name="indicacaoReferencia"></a>
						<table class="subformulario" style="width:97%;margin:auto;margin-bottom:20px;border:1px solid #DDDDDD;">
							<caption>Referências</caption>
							<tr>
								<td>
								
									<div class="descricaoOperacao">
										Indique abaixo referências para materiais que auxiliarão os alunos no aprendizado do conteúdo a ser ministrado.<br/>Se o material for um livro, poderá ser consultado no acervo das bibliotecas da instituição.
									</div>
									<a4j:outputPanel id="formIR">
									<table class="subformulario" style="width:80%;" cellspacing="0" cellpadding="0">
										<caption>Nova Indicação de Referência</caption>
										<tr><td style="border-left:1px solid #C8D5EC;border-right:1px solid #C8D5EC;">
											<div class="divLinha">
												<label class="labelLinha">Tipo:<span class="required">&nbsp;</span></label>
												<h:selectOneRadio id="tipo" value="#{planoCurso.tipoIR}" valueChangeListener="#{planoCurso.mudarTipo}">
													<f:selectItem itemValue="L" itemLabel = "Livro" />
													<f:selectItem itemValue="A" itemLabel = "Artigo" />
													<f:selectItem itemValue="R" itemLabel = "Revista" />
													<f:selectItem itemValue="S" itemLabel = "Site" />
													<f:selectItem itemValue="O" itemLabel = "Outro" />
													<a4j:support event="onclick" oncomplete="exibeCampos(this, null,'#{planoCurso.alterarReferencia}');" onsubmit="true" reRender="formIR,panelIR" />
												</h:selectOneRadio>
											</div>
											
											<div id="linhaNome" class="divLinha"><label class="labelLinha">Nome ou Título:<span class="required">&nbsp;</span></label><h:inputText id="descricao" value="#{planoCurso.indicacaoReferencia.descricao}" style="width:300px;" /></div>
											<div id="linhaPesquisarTitulo" class="divLinha">												
												<style>
													.listaOpcoes li a { background-image: url(/sigaa/img/buscar.gif); }
												</style>
												
												<ul class="listaOpcoes" style="border:none;margin:auto;width:250px;margin-bottom:30px;">
													<li>
														<h:commandLink action="#{planoCurso.pesquisarNoAcervo}" value="Pesquisar no Acervo" />
													</li>
												</ul>
												
												<div id="linhaIRAssociada" class="descricaoOperacao" style="text-align:center;display:none;">
													<c:if test="${planoCurso.indicacaoReferencia.tituloCatalografico != null}">
														Esta referência bibliográfica está contida no acervo bibliográfico da instituição. Informe se é Básica ou Complementar e clique em <strong>Adicionar Referência</strong> para confirmar.
													</c:if>
													<c:if test="${planoCurso.indicacaoReferencia.tituloCatalografico == null}">
														Esta referência bibliográfica <strong>não</strong> está contida no acervo bibliográfico da instituição. Informe seus dados e clique em <strong>Adicionar Referência</strong> para confirmar.
													</c:if>
												</div>
											</div>
											
											<div id="linhaTitulo" class="divLinha">
												<label class="labelLinha">Título: <span class="required">&nbsp;</span></label>
												<h:inputText id="titulo" value="#{planoCurso.indicacaoReferencia.titulo}" style="width:300px;" />
												<span id="titulo"><h:outputText value="#{planoCurso.indicacaoReferencia.titulo}" rendered="#{planoCurso.indicacaoReferencia.tituloCatalografico != null}" /></span>
											</div>
											<div id="linhaAutor" class="divLinha">
												<label class="labelLinha">Autor: <span class="required">&nbsp;</span></label>
												<h:inputText id="autor" value="#{planoCurso.indicacaoReferencia.autor}" style="width:300px;" />
												<span id="autor"><h:outputText value="#{planoCurso.indicacaoReferencia.autor}" rendered="#{planoCurso.indicacaoReferencia.tituloCatalografico != null}" /></span>
											</div>
											<div id="linhaEditora" class="divLinha">
												<label class="labelLinha">Editora: <span class="required">&nbsp;</span></label>
												<h:inputText id="editora" value="#{planoCurso.indicacaoReferencia.editora}" style="width:300px;" />
												<span id="editora"><h:outputText value="#{planoCurso.indicacaoReferencia.editora}" rendered="#{planoCurso.indicacaoReferencia.tituloCatalografico != null}" /></span>
											</div>
											<div id="linhaAno" class="divLinha">
												<label class="labelLinha">Ano: <span class="required">&nbsp;</span></label>
												<h:inputText id="ano" value="#{planoCurso.indicacaoReferencia.ano}" style="width:100px;" />
												<span id="ano"><h:outputText value="#{planoCurso.indicacaoReferencia.ano}" rendered="#{planoCurso.indicacaoReferencia.tituloCatalografico != null}" /></span>
											</div>
											<div id="linhaEdicao" class="divLinha">
												<label class="labelLinha">Edição:</label>
												<h:inputText id="edicao" value="#{planoCurso.indicacaoReferencia.edicao}" style="width:100px;" />
												<span id="edicao"><h:outputText value="#{planoCurso.indicacaoReferencia.edicao}" rendered="#{planoCurso.indicacaoReferencia.tituloCatalografico != null}" /></span>
											</div>
											
											<div id="linhaURL" class="divLinha">
												<label class="labelLinha">Endereço (URL):<span id="obrigatorioURL" class="required">&nbsp;</span></label>
												<h:inputText id="url" value="#{planoCurso.indicacaoReferencia.url}" style="width:300px;" />
											</div>
											
											<div id="linhaTipoIR" class="divLinha">
												<label class="labelLinha">Tipo da Referência:<span id="obrigatorioURL" class="required">&nbsp;</span></label>
												<h:selectOneRadio id="tipoIR" value="#{planoCurso.tipoIndicacaoReferencia}">
													<f:selectItem itemValue="1" itemLabel="Básica" />
													<f:selectItem itemValue="2" itemLabel="Complementar" />
												</h:selectOneRadio>
											</div>
										</td></tr>
										<tfoot>
											<tr><td colspan="2">
												<a4j:commandButton id="adicionarIR" value="Adicionar Referência" actionListener="#{planoCurso.adicionarIndicacaoReferencia}" rendered="#{!planoCurso.alterarReferencia}" reRender="formIR,panelIR,listaIndicacoesBasicas;" onclick="configuraTitulo();" oncomplete="exibeCampos(null,'#{planoCurso.tipoIR}', false);"/>
												<a4j:commandButton id="alterarIR" value="Alterar Referência" actionListener="#{planoCurso.alterarIndicacaoReferencia}" rendered="#{planoCurso.alterarReferencia}" reRender="formIR,panelIR,listaIndicacoesBasicas;" onclick="configuraTitulo();" oncomplete="exibeCampos(null,'#{planoCurso.tipoIR}',false);"/>
												<a4j:commandButton value="Limpar" actionListener="#{planoCurso.limparReferencia}" reRender="formIR,panelIR" oncomplete="zeraIR(true);" />
											</td></tr>
										</tfoot>
									</table>
									</a4j:outputPanel>
									
									<a4j:outputPanel id="panelIR">
										<c:set var="mensagemSucessoReferencia" value="#{planoCurso.mensagemSucessoReferencia}" />
										<c:set var="mensagemErroReferencia" value="#{planoCurso.mensagemErroReferencia}" />
										
										<a4j:outputPanel rendered="#{mensagemSucessoReferencia != null }">
											<h:outputText value="#{planoCurso.mensagemSucessoReferencia}" style="color:#00AA00;margin:10px;text-align:center;display:block;font-weight:bold;" escape="false" />
											<script>exibeCamposLivro = false;</script>
										</a4j:outputPanel>
										
										<h:outputText value="#{planoCurso.mensagemErroReferencia}" style="color:#AA0000;margin:10px;text-align:center;display:block;font-weight:bold;" escape="false" />
										<c:if test="${empty planoCurso.referencias}">
											<div class="listaVazia">Nenhuma referência cadastrada.</div>
										</c:if>
										
										<c:if test="${not empty planoCurso.referencias}">
											<div class="infoAltRem">
												<h:graphicImage value="/ava/img/page_edit.png" /><h:outputText style="font-weight:bold;" value=": Alterar Indicação de Referência" />
												<h:graphicImage value="/img/delete.gif" /><h:outputText style="font-weight:bold;" value=": Remover Indicação de Referência" />
												<h:graphicImage value="/img/book_blue_view_peq.png" /><h:outputText style="font-weight:bold;" value=": Visualizar Informações sobre os Exemplares" />
												<br/>
												<h:graphicImage value="/img/arrow_up.png" /><h:outputText style="font-weight:bold;" value=": Mudar o tipo da indicação para Básica" />
												<h:graphicImage value="/img/arrow_down.png" /><h:outputText style="font-weight:bold;" value=": Mudar o tipo da indicação para Complementar" />
											</div>
											
											<div class="infoAltRem"><h:graphicImage value="/img/destaque.png" />: Livro associado a um material da biblioteca</div>
											
											<div style="background:#CCCCCC;text-align:center;font-weight:bold;">Básicas</div>
											
											<t:dataTable id="listaIndicacoesBasicas"  value="#{planoCurso.referenciasBasicas}" var="r" rowClasses="linhaPar,linhaImpar" style="width:100%;">
												<t:column>
													<f:facet name="header"><h:outputText value="Tipo de material" /></f:facet>
													<h:outputText value="#{r.tipoDesc}"/>
												</t:column>
												<t:column style="width:80%;">
													<f:facet name="header"><h:outputText value="Descrição" /></f:facet>
													<h:graphicImage value="/img/destaque.png" rendered="#{r.tituloCatalografico != null}" />
													<h:outputText value="#{r.descricao}" escape="false" />
												</t:column>
												<t:column>
													<a4j:commandLink actionListener="#{planoCurso.mudarTipoIndicacaoParaComplementar}" reRender="panelIR" title="Mudar o tipo da indicação para Complementar">
														<f:param name="indiceIR" value="#{r.id}" />
														<h:graphicImage value="/img/arrow_down.png" />
													</a4j:commandLink>
												</t:column>
												<t:column>
													<c:set var="idReferencia" value="#{ r.id }" />
													<%@include file="/ava/PlanoCurso/link_informacoes_exemplares.jsp" %>									
												</t:column>
												<t:column>
													<a4j:commandLink actionListener="#{planoCurso.preEditarReferencia}" rendered="#{r.tituloCatalografico == null}"  oncomplete="exibeCampos(null,'#{planoCurso.tipoIR}',true);" reRender="formIR,panelIR,listaIndicacoesBasicas,listaIndicacoesComplementares" title="Alterar Indicação de Referência">
														<f:param name="indiceIR" value="#{r.id}" />
														<h:graphicImage value="/ava/img/page_edit.png" />
													</a4j:commandLink>
												</t:column>
												<t:column>
													<a4j:commandLink actionListener="#{planoCurso.removerIndicacaoReferencia}" oncomplete="zeraIR(true);" onclick="if (!confirm('Confirma a remoção desta Indicação de Referência?')) return false;" reRender="panelIR,listaIndicacoesBasicas" title="Remover Indicação de Referência">
														<f:param name="indiceIR" value="#{r.id}" />
														<h:graphicImage value="/img/delete.gif" />
													</a4j:commandLink>
												</t:column>
											</t:dataTable>
											
											<div style="margin-top:10px;background:#CCCCCC;text-align:center;font-weight:bold;">Complementares</div>
											
											<t:dataTable id="listaIndicacoesComplementares" value="#{planoCurso.referenciasComplementares}" var="r" rowClasses="linhaPar,linhaImpar" style="width:100%;">
												<t:column>
													<f:facet name="header"><h:outputText value="Tipo de material" /></f:facet>
													<h:outputText value="#{r.tipoDesc}"/>
												</t:column>
												<t:column width="80%">
													<f:facet name="header"><h:outputText value="Descrição" /></f:facet>
													<h:graphicImage value="/img/destaque.png" rendered="#{r.tituloCatalografico != null}" />
													<h:outputText value="#{r.descricao}" escape="false" />
												</t:column>
												<t:column>
													<a4j:commandLink actionListener="#{planoCurso.mudarTipoIndicacaoParaBasica}" reRender="panelIR" title="Mudar o tipo da indicação para Básica">
														<f:param name="indiceIR" value="#{r.id}" />
														<h:graphicImage value="/img/arrow_up.png" />
													</a4j:commandLink>
												</t:column>
												<t:column>
													<c:set var="idReferencia" value="#{ r.id }" />
													<%@include file="/ava/PlanoCurso/link_informacoes_exemplares.jsp" %>									
												</t:column>
										
												<t:column>
													<a4j:commandLink actionListener="#{planoCurso.preEditarReferencia}" rendered="#{r.tituloCatalografico == null}" oncomplete="exibeCampos(null,'#{planoCurso.tipoIR}',true);" reRender="formIR,panelIR,listaIndicacoesBasicas,listaIndicacoesComplementares" title="Alterar Indicação de Referência">
														<f:param name="indiceIR" value="#{r.id}" />
														<h:graphicImage value="/ava/img/page_edit.png" />
													</a4j:commandLink>
												</t:column>
												
												<t:column>
													<a4j:commandLink actionListener="#{planoCurso.removerIndicacaoReferencia}" oncomplete="zeraIR(true);" onclick="if (!confirm('Confirma a remoção desta Indicação de Referência?')) return false;" reRender="panelIR,listaIndicacoesComplementares" title="Remover Indicação de Referência">
														<f:param name="indiceIR" value="#{r.id}" />
														<h:graphicImage value="/img/delete.gif" />
													</a4j:commandLink>
												</t:column>
											</t:dataTable>
										</c:if>
									</a4j:outputPanel>
									
								</td>
							</tr>
						</table>
					</td>
				</tr>
							
				
				<tfoot>
					<tr>
						<td>
							<h:commandButton value="Salvar" action="#{planoCurso.salvar}" id="botaoSalvar"/>
							<h:commandButton value="Salvar e Enviar" action="#{planoCurso.salvarEFinalizar}" id="botaoSalvarEFinalizar"/>
							<h:commandButton value="Gerenciar Outros Planos" action="planoCurso" onclick="return confirm('Deseja realmente sair dessa página? Todos os dados não salvos serão perdidos!')" />
							<h:commandButton value="<< Voltar" action="#{planoCurso.voltar}" id="voltar" />
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{planoCurso.cancelar}" id="botaoCancelar" />
						</td>
					</tr>
				</tfoot>
			</table>
			

			
		</h:form>
	</a4j:region>
	
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	
	<script type="text/javascript" charset="ISO-8859">
						var J = jQuery.noConflict();
	</script>
		
	<script>
		var exibeCamposLivro = false;
		
		function exibeCampos(select, tipo, alterar){

			var auxTipo = "L";

			if (select != null){
				auxTipo = select.value
				zeraIR(false);
			} else {
				if (tipo != null)
					auxTipo = tipo;
			}

			document.getElementById("linhaNome").style.display = "none";
			document.getElementById("linhaPesquisarTitulo").style.display = "none";
			document.getElementById("linhaTitulo").style.display = "none";
			document.getElementById("linhaAutor").style.display = "none";
			document.getElementById("linhaEditora").style.display = "none";
			document.getElementById("linhaAno").style.display = "none";
			document.getElementById("linhaEdicao").style.display = "none";
			document.getElementById("linhaURL").style.display = "none";
			document.getElementById("obrigatorioURL").style.display = "none";
			document.getElementById("linhaIRAssociada").style.display = "none";
			document.getElementById("linhaTipoIR").style.display = "none";

			document.getElementById("form:titulo").style.display = "inline";
			document.getElementById("form:autor").style.display = "inline";
			document.getElementById("form:ano").style.display = "inline";
			document.getElementById("form:editora").style.display = "inline";
			document.getElementById("form:edicao").style.display = "inline";
			
			if (auxTipo == "L"){

				alterar = String(alterar) == 'true';
				
				if ( alterar )
					exibeCamposLivro = true;
				
				if ( !alterar )
					document.getElementById("linhaPesquisarTitulo").style.display = "block";
									
				if (exibeCamposLivro){
					document.getElementById("linhaTitulo").style.display = "block";
					document.getElementById("linhaAutor").style.display = "block";
					document.getElementById("linhaEditora").style.display = "block";
					document.getElementById("linhaAno").style.display = "block";
					document.getElementById("linhaEdicao").style.display = "block";
					document.getElementById("linhaIRAssociada").style.display = "block";
					document.getElementById("linhaTipoIR").style.display = "block";
				}

			} else { 
				if (auxTipo == "A") {
					document.getElementById("linhaNome").style.display = "block";
					document.getElementById("linhaAutor").style.display = "block";
					document.getElementById("linhaAno").style.display = "block";
					document.getElementById("linhaURL").style.display = "block";
				} else if (auxTipo == "R") {
					document.getElementById("linhaNome").style.display = "block";
					document.getElementById("linhaAutor").style.display = "block";
					document.getElementById("linhaAno").style.display = "block";
					document.getElementById("linhaEditora").style.display = "block";
					document.getElementById("linhaEdicao").style.display = "block";
					document.getElementById("linhaURL").style.display = "block";
				} else if (auxTipo == "S") {
					document.getElementById("obrigatorioURL").style.display = "inline";
					document.getElementById("linhaNome").style.display = "block";
					document.getElementById("linhaURL").style.display = "block";
				} else {
					document.getElementById("linhaNome").style.display = "block";
					document.getElementById("linhaURL").style.display = "block";
				}

				document.getElementById("linhaTipoIR").style.display = "block";
			}
		}

		function atualizaData(inicial,cadastro,atualizacao,ead){
			var inicio = null;
			var fim = null;

			if (cadastro){
				inicio = document.getElementById("form:inicioTA");
				fim = document.getElementById("form:fimTA");
			} else if (atualizacao) {
				inicio = document.getElementById("form:inicioTAAlterar");
				fim = document.getElementById("form:fimTAAlterar");
			}else if (ead){
				inicio = document.getElementById("form:inicioTAEad");
				fim = document.getElementById("form:fimTAEad");
			}
			
			if (inicial) {
				if (inicio.value > fim.value)
					fim.value = inicio.value;
			} else 
				if (fim.value < inicio.value)
					inicio.value = fim.value;			
		}
		
		function zeraTA (){
			document.getElementById("form:descricaoTA").value = "";
			document.getElementById("form:conteudoTA").value = "";
		}

		function zeraAV (){
			document.getElementById("form:descricaoAV").value = "0";
			document.getElementById("form:dataAV").value = "";
			document.getElementById("form:horaAV").value = "";
		}

		function zeraIR (reiniciarTipo){
			if (reiniciarTipo == true)
				document.getElementById("form:tipo:0").checked = true;
			document.getElementById("form:titulo").value = "";
			document.getElementById("form:descricao").value = "";
			document.getElementById("form:autor").value = "";
			document.getElementById("form:editora").value = "";
			document.getElementById("form:ano").value = "";
			document.getElementById("form:edicao").value = "";
			document.getElementById("form:url").value = "http://";
			document.getElementById("form:tipoIR:0").checked = false;
			document.getElementById("form:tipoIR:1").checked = false;
			
			document.getElementById("titulo").innerHTML = "";
			document.getElementById("autor").innerHTML = "";
			document.getElementById("editora").innerHTML = "";
			document.getElementById("ano").innerHTML = "";
			document.getElementById("edicao").innerHTML = "";

			exibeCamposLivro = false;
			
			exibeCampos(null, null, false);
		}

		function configuraTitulo () {
		}

		<c:if test="${planoCurso.livroSelecionado == true}">
			exibeCamposLivro = true;
		</c:if>

		<c:if test="${planoCurso.scrollIR == true}">
			document.getElementById('indicacaoReferencia').scrollIntoView(true);
		</c:if>
		
		exibeCampos(null, null, false);
		<c:if test="${planoCurso.livroSelecionado == false}">
			zeraIR(true);
		</c:if>

		<c:if test="${planoCurso.indicacaoReferencia.tituloCatalografico != null}">
			document.getElementById("form:titulo").style.display = "none";
			document.getElementById("form:autor").style.display = "none";
			document.getElementById("form:ano").style.display = "none";
			document.getElementById("form:editora").style.display = "none";
			document.getElementById("form:edicao").style.display = "none";
		</c:if>

		function exibeAvisoSalvar () {
			J("#avisoSalvar").show();
		}

		function escondeAvisoSalvar () {
			J("#avisoSalvar").hide();
		}

		function atualizaConteudo(radio){
			var idItemEad = document.getElementById("form:idItemEad");
					
			idItemEad.value = radio.value;	
		}
	</script>
	
	<div id="avisoSalvar" style="position:fixed;bottom:0px;right:0px;backround:#FFF;border:1px solid #CCC;padding:5px;display:none;">Salvando automaticamente ...</div>
	
	<%@include file="/ava/PlanoCurso/panel_informacoes_exemplares.jsp"%>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
