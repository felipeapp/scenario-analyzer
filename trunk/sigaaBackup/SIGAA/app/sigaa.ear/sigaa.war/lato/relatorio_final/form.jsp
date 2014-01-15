<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:outputText value="#{relatorioFinalLato.create }"></h:outputText>
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> &gt; Submissão de Relatório Final de Curso</h2>

<div class="descricaoOperacao">
	<p>
		Parecer sobre o relatório:
	</p>
	<p style="padding-left: 50px;">
		${relatorioFinalLato.historico.parecer }
	</p>
</div>

<h:form id="cadastroRelatorio" enctype="multipart/form-data">
	<h:inputHidden value="#{ relatorioFinalLato.obj.curso.id }"/>
		<table class="formulario">
		<caption class="formulario">Dados do Relatório Final de Curso</caption>
		<tr><td>
			<table class="subFormulario" width="100%">
			<caption>Resumo da Realização do Curso</caption>
			<tr>
				<th>Curso:</th>
				<td><h:outputText id="nomeCurso" value="#{ relatorioFinalLato.obj.curso.descricao }"/> </td>
			</tr>
			<tr>
				<th>Remunerado:</th>
				<td>
					<c:if test="${ relatorioFinalLato.obj.curso.cursoPago }">Sim</c:if>
					<c:if test="${ not relatorioFinalLato.obj.curso.cursoPago }">Não</c:if>
				</td>
			</tr>
			<tr>
				<th class="required">Número do Processo:</th>
				<td>
					23077.<h:inputText id="numeroProcesso" value="#{ relatorioFinalLato.obj.numeroProcesso }" size="10" maxlength="15"/>/
					<h:inputText id="anoProcesso" value="#{ relatorioFinalLato.obj.anoProcesso }" size="4" maxlength="4" onkeyup="formatarInteiro(this);"/>-XX
					<ufrn:help>Informe apenas a parte do número após o ponto e o ano do processo</ufrn:help>
				</td>
			</tr>
			<tr>
				<th>Unidade Responsável:</th>
				<td><h:outputText id="unidadeResponsavel" value="#{ relatorioFinalLato.obj.curso.unidade.nome }"/></td>
			</tr>
			<tr>
				<th>Centro:</th>
				<td><h:outputText id="centro" value="#{ relatorioFinalLato.obj.curso.unidade.gestora.nome }"/></td>
			</tr>
			<tr class="linhaImpar">
				<th valign="top">Coordenador:</th>
				<td>
					<table>
						<tr>
							<td colspan="2"><h:outputText id="nomeCoordenador" value="#{ relatorioFinalLato.obj.curso.coordenacao.servidor.nome }"/></td>
						</tr>
						<tr>
							<th>Titulação:</th>
							<td><h:outputText id="tituloCoordenador" value="#{ relatorioFinalLato.obj.curso.coordenacao.servidor.formacao.denominacao }"/></td>
						</tr>
						<tr>
							<th>Telefone:</th>
							<td><h:outputText id="foneCoordenador" value="#{ relatorioFinalLato.obj.curso.coordenacao.telefoneContato1 }"/></td>
						</tr>
						<tr>
							<th>Email:</th>
							<td><h:outputText id="mailCoordenador" value="#{ relatorioFinalLato.obj.curso.coordenacao.emailContato }"/></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<th valign="top">Vice-Coordenador:</th>
				<td>
					<table>
						<tr>
							<td colspan="2"><h:outputText id="nomeVice" value="#{ relatorioFinalLato.obj.curso.viceCoordenacao.servidor.nome }"/></td>
						</tr>
						<tr>
							<th>Titulação:</th>
							<td><h:outputText id="tituloVice" value="#{ relatorioFinalLato.obj.curso.viceCoordenacao.servidor.formacao.denominacao }"/></td>
						</tr>
						<tr>
							<th>Telefone:</th>
							<td><h:outputText id="foneVice" value="#{ relatorioFinalLato.obj.curso.viceCoordenacao.telefoneContato1 }"/></td>
						</tr>
						<tr>
							<th>Email:</th>
							<td><h:outputText id="mailVice" value="#{ relatorioFinalLato.obj.curso.viceCoordenacao.emailContato }"/></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr class="linhaImpar">
				<th>Período de realização:</th>
				<td>
					<table>
						<tr>
							<th>Previsto:</th>
							<td><h:outputText id="dataIncioPrevista" value="#{ relatorioFinalLato.obj.curso.dataInicio }"/> a <h:outputText id="dataFimPrevista" value="#{ relatorioFinalLato.obj.curso.dataFim }"/></td>
						</tr>
						<tr>
							<th class="required">Realizado:</th>
							<td>
								<t:inputCalendar id="dataInicioRealizada" value="#{ relatorioFinalLato.obj.dataInicioRealizado }" 
									renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="formataData(this,event)"/> a
								<t:inputCalendar id="dataFimRealizada" value="#{ relatorioFinalLato.obj.dataFimRealizado }" 
									renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="formataData(this,event)"/>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<th>Portaria de Aprovação:</th>
				<td>
					<table>
						<tr>
							<th>Número:</th>
							<td>
								<h:outputText value="#{cursoLatoForm.obj.propostaCurso.numeroProcesso}" />
							</td>
						</tr>
						<tr>
							<th style="text-align: left;">Data:</th>
								<td>
									<h:outputText value="#{cursoLatoForm.obj.propostaCurso.dataPublicacaoPortaria}" />
								</td>
						</tr>
					</table>				
				</td>
			</tr>
			<tr class="linhaImpar">
				<th>Carga Horária:</th>
				<td>
					<table>
						<tr>
							<th>Prevista:</th>
							<td><h:outputText id="chPrevista" value="#{ relatorioFinalLato.obj.curso.cargaHoraria }"/> horas</td>
						</tr>
						<tr>
							<th class="required">Realizada:</th>
							<td><h:inputText id="chRealizada" value="#{ relatorioFinalLato.obj.chRealizada }" size="3"/> horas</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<th>Discentes:</th>
				<td>
					<table>
						<tr>
							<th class="required">Inscritos:</th><td><h:inputText id="inscritos" value="#{ relatorioFinalLato.obj.numeroInscritos }" size="3" onkeyup="formatarInteiro(this);"/></td>
							<th class="required">Selecionados:</th><td><h:inputText id="selecionados" value="#{ relatorioFinalLato.obj.numeroSelecionados }" size="3" onkeyup="formatarInteiro(this);"/></td>
							<th class="required">Matriculados:</th><td><h:inputText id="matriculados" value="#{ relatorioFinalLato.obj.numeroMatriculados }" size="3" onkeyup="formatarInteiro(this);"/></td>
							<th class="required">Concluintes:</th><td><h:inputText id="concluintes" value="#{ relatorioFinalLato.obj.numeroConcluintes }" size="3" onkeyup="formatarInteiro(this);"/></td>
						</tr>
						<tr>
							<th class="required">Outra IES:</th><td><h:inputText id="outraIES" value="#{ relatorioFinalLato.obj.numeroOutraIes }" size="3" onkeyup="formatarInteiro(this);"/></td>
							<th class="required">Prof. Ed. Básica:</th><td><h:inputText id="profEdBas" value="#{ relatorioFinalLato.obj.numeroProfEdBasica }" size="3" onkeyup="formatarInteiro(this);"/></td>
							<th class="required">Profissional Liberal:</th><td><h:inputText id="profLiberal" value="#{ relatorioFinalLato.obj.numeroProfissionalLiberal }" size="3" onkeyup="formatarInteiro(this);"/></td>
							<th class="required">Executivos:</th><td><h:inputText id="executivos" value="#{ relatorioFinalLato.obj.numeroExecutivos }" size="3" onkeyup="formatarInteiro(this);"/></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<th>Docentes:</th>
				<td>
					<table>
						<tr>
							<th>Outras IES:</th>
							<td><h:outputText id="docentesOutraIEs" value="#{ relatorioFinalLato.numeroDocentesOutrasIes }"/></td>
							<th>${configSistema['siglaInstituicao']}:</th>
							<td><h:outputText id="docentesUFRN" value="#{ relatorioFinalLato.numeroDocentesUFRN }"/></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		</td></tr>
			<tr>
				<td>
					<div id="tabs1-relatorioFinal" class="reduzido">
						<div id="introducao">
							<table width="100%" class="subFormulario">
							<caption>Introdução</caption>
								<tr>
								<td>
									<h:inputTextarea id="intro"	value="#{ relatorioFinalLato.obj.introducao }" cols="100" rows="6" />
								</td>
								</tr>
							</table>
						</div>
						
						<div id="selecao" class="aba">
							<table width="100%" class="subFormulario">
							<caption>Seleção</caption>
								<tr>
								<td width="40"><html:img page="/img/help.png"/> </td>
								<td valign="center" style="text-align: justify">
								Informar os métodos adotados, período, relação dos inscritos e de aprovados,
								procedência dos candidatos, etc. <br/>
								</td>
								</tr>
								<tr>
								<td colspan="2">
									<h:inputTextarea id="selecao" value="#{ relatorioFinalLato.obj.selecao }" cols="100" rows="6" />
								</td>
								</tr>
							</table>
						</div>
						
						<div id="disciplinas" class="aba">
							<table width="100%" class="subFormulario">
							<caption>Disciplinas</caption>
								<tr>
								<td width="40"><html:img page="/img/help.png"/> </td>
								<td valign="center" style="text-align: justify">
								Informar a relação das disciplinas oferecidas, carga horária e o período de realização.
								Se houve alguma alteração em relação ao proposto no projeto, a mesma deve ser justificada.
								Informar ainda os critérios utilizados para avaliação dos alunos, trabalhos, entrevistas, monografias,
								nota mínima para aprovação, freqüência mínima, etc. <br/>
								</td>
								</tr>
								<tr>
								<td colspan="2">
									<h:inputTextarea id="disciplinas" value="#{ relatorioFinalLato.obj.disciplinas }" cols="100" rows="6" />
								</td>
								</tr>
							</table>
						</div>
						
						<div id="realizacao" class="aba">
							<table width="100%" class="subFormulario">
							<caption>Realização</caption>
								<tr>
								<td width="40"><html:img page="/img/help.png"/> </td>
								<td valign="center" style="text-align: justify">
								Incluir informações que julgue necessário, tais como: dificuldades, atrasos, substituição
								de docentes, alunos desistentes, etc. <br/>
								</td>
								</tr>
								<tr>
								<td colspan="2">
									<h:inputTextarea id="realizacao" value="#{ relatorioFinalLato.obj.realizacao }" cols="100" rows="6" />
								</td>
								</tr>
							</table>
						</div>
						
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<table width="100%" class="subFormulario" id="disciplinas">
						<caption>Disciplinas</caption>
					       <c:forEach items="#{relatorioFinalLato.disciplinas}" var="ccl" varStatus="status">
					            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					            	<td><b>Disciplina</b><br /></td>
					            	<td>${ccl.codigo}</td>
		 			                <td>${ccl.nomeCurso}</td>
					                <td style="text-align: right;">${ccl.cargaHorariaTotal} h</td>
					            </tr>
					            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					            	<td colspan="5"><b>Ementa:</b><br />${ccl.ementa}</td>
					            </tr>
					            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">	            	
					            	<td colspan="5"><b>Bibliografia:</b><br />${ccl.bibliografia}</td>
					            </tr>
					            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">	            	
					            	<td colspan="5"><b>Docente(s):</b></td>
					            </tr>
			            		<c:forEach items="#{ccl.nomeDocente}" var="nomes">
			          				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
			         					<td colspan="3">${nomes.key}</td>
			          					<td style="text-align: right;">${nomes.value} h</td>
			          				</tr>
			          			</c:forEach>
					       </c:forEach>
					</table>					
				</td>
			</tr>
			
			<tr>
				<td>
					<table width="100%" class="subFormulario" id="disciplinas">
						<caption>Processo Seletivo</caption>
						<thead>
							<tr>
								<td style="text-align: left;">Processo Seletivo</td>
								<td style="text-align: right;">Cand. Inscritos</td>
								<td style="text-align: right;">Cand. Aprovados</td>
								<td style="text-align: right;">Cand. Cancelados</td>
								<td style="text-align: right;">Cand. Deferidos</td>
								<td style="text-align: right;">Cand. Indeferidos</td>
								<td style="text-align: right;">Cand. Suplentes</td>
							</tr>
						</thead>
						
						
						<c:forEach var="linha" items="#{relatorioFinalLato.quantitativoProcessoSeletivo}" varStatus="status">

					        <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"><tr>
								<td style="text-align: left;">${ linha.key }</td>
						
								<c:forEach var="coluna" items="${ linha.value }">
									<td style="text-align: right;">${ coluna }</td>	
								</c:forEach>
						
							</tr>						

						</c:forEach>
						
					</table>
				</td>
			</tr>
			
			<tr>
				<td>
					<table width="100%" class="subFormulario" id="disciplinas">
						<caption>Conceito</caption>

						   <c:set var="_disciplina" />			
					       <c:forEach items="#{relatorioFinalLato.componentes}" var="comp" varStatus="status">
						   <c:set var="disciplinaAtual" value="${ comp.componente.id }"/>
				
						   <c:if test="${_disciplina != disciplinaAtual}">

						       	<thead>
									<tr>
										<th colspan="4"> ${ comp.componente.codigo } - ${ comp.componente.nome } </th>
									</tr>
									<tr>
										<th style="text-align: left;"> Discente </th>
										<th style="text-align: right;"> Numero de Faltas </th>
										<th style="text-align: right; padding-right: 10px;"> Média Final </th>
										<th style="text-align: left;"> Situação </th>
									</tr>
								</thead>
							
							</c:if>

					            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
									<td style="text-align: left;"> ${ comp.discente.pessoa.nome } </td>
									<td style="text-align: right;"> ${ comp.numeroFaltas } </td>
									<td style="text-align: right; padding-right: 10px;"> ${ comp.mediaFinal } </td>
									<td style="text-align: left;"> ${ comp.discente.statusString } </td>
		          				</tr>

								<c:set var="_disciplina" value="${ disciplinaAtual }"/>
													     
					       </c:forEach>
					</table>					
				</td>
			</tr>
			<tr>
				<td>
					<div id="tabs2-relatorioFinal" class="reduzido">
					
						<div id="recursos" class="aba">
							<table width="100%" class="subFormulario" id="aviso">
							<caption>Recursos financeiros e aplicação dos recursos</caption>
								<tr>
									<td width="40"><html:img page="/img/help.png"/> </td>
									<td valign="center" style="text-align: justify">
									Utilize este espaço para anexar documentos da parte financeira do curso
									em conformidade com o formulário da FUNPEC, além do quadro demonstrativo
									das despesas realizadas.	<br/>
									</td>
								</tr>
								<tr>
									<th width="20%"> Descrição:</th>
									<td>
										<span class="required"></span>
										<h:inputText  id="descricao" value="#{relatorioFinalLato.descricaoArquivo}" size="83" maxlength="150"/>
									</td>
								</tr>
								<tr>
									<th width="20%">Arquivo:</th>
									<td>
										<span class="required"></span>
										<t:inputFileUpload id="uFile" value="#{relatorioFinalLato.file}" storage="file" size="70"/>
									</td>
								</tr>
								<tr>
									<td colspan="2" align="center">
										<br/>
										<h:commandButton	action="#{relatorioFinalLato.anexarArquivo}" value="Anexar Arquivo"/>
										<br/>
									</td>
								</tr>
								<c:if test="${not empty relatorioFinalLato.obj.arquivos}">
								<tr>
									<td colspan="2" class="subFormulario">	Lista de Arquivos anexados com sucesso </td>
								</tr>
							
								<tr>
									<td colspan="2">
										<input type="hidden" value="0" id="idArquivo" name="idArquivo"/>
							
										<t:dataTable id="dataTableArquivos" value="#{relatorioFinalLato.obj.arquivos}" var="anexo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
							
											<t:column  width="97%">
												<f:facet name="header"><f:verbatim>Descrição do Arquivo</f:verbatim></f:facet>
												<h:outputText value="#{anexo.descricao}" />
											</t:column>
							
											<t:column>
												<h:commandButton image="/img/delete.gif" action="#{relatorioFinalLato.removeAnexo}"
													title="Remover Arquivo"  alt="Remover Arquivo"   onclick="$(idArquivo).value=#{anexo.idArquivo}" id="remArquivo" />
											</t:column>
							
										</t:dataTable>
									</td>
								</tr>
								</c:if>
							</table>
						</div>
						
						<div id="divulgacao" class="aba">
							<table width="100%" class="subFormulario" id="aviso">
							<caption>Meios de Divulgação</caption>
								<tr>
								<td>
									<h:inputTextarea id="divulgacao" value="#{ relatorioFinalLato.obj.meiosDivulgacao }" cols="100" rows="6" />
								</td>
								</tr>
							</table>
						</div>
						
						<div id="instituicoes" class="aba">
							<table width="100%" class="subFormulario" id="aviso">
							<caption>Instituições Envolvidas</caption>
								<tr>
								<td>
									<h:inputTextarea id="instituicoes" value="#{ relatorioFinalLato.obj.instituicoes }" cols="100" rows="6" />
								</td>
								</tr>
							</table>
						</div>
						
						<div id="conclusao" class="aba">
							<table width="100%" class="subFormulario">
							<caption>Conclusão</caption>
								<tr>
								<td width="40"><html:img page="/img/help.png"/> </td>
								<td valign="center" style="text-align: justify">
								Avaliação geral da realização do curso em termos dos objetivos propostos.
								<br/>
								</td>
								</tr>
								<tr>
								<td colspan="2">
									<h:inputTextarea id="conclusao" value="#{ relatorioFinalLato.obj.conclusao }" cols="100" rows="6" />
								</td>
								</tr>
							</table>
						</div>
					</div>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Cancelar" action="#{relatorioFinalLato.cancelar}" />
						<h:commandButton value="Gravar"	action="#{relatorioFinalLato.gravar}" />
						<h:commandButton value="Resumo >>"	action="#{relatorioFinalLato.verResumo}" />
					</td>
				</tr>
			</tfoot>
		</table>
		
		<script>
		var Abas = function() {
			return {
			    init : function(){
			        var abas = new YAHOO.ext.TabPanel('tabs1-relatorioFinal');
			        abas.addTab('introducao', "Introdução");
			        abas.addTab('selecao', "Seleção");
			        abas.addTab('disciplinas', "Disciplinas");
			        abas.addTab('realizacao', "Informações Complementares");
			        var abas2 = new YAHOO.ext.TabPanel('tabs2-relatorioFinal');
			        abas2.addTab('recursos', "Recursos Financeiros");
			        abas2.addTab('divulgacao', "Meios de Divulgação");
			        abas2.addTab('instituicoes', "Instituições Envolvidas");
			        abas2.addTab('conclusao', "Conclusão");
			        
			        abas.activate('introducao');
				    abas2.activate('recursos');
			    }
		    }
		}();
		YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
		</script>
	
</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

	<script type="text/javascript">$('cadastroRelatorio:numeroProcesso').focus();</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
