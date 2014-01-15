<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.FuncaoMembro"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>


<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>
<f:view>
	<h2>Visualiza��o da A��o de Extens�o</h2>
	<br>

	<h:form>

		 <c:set var="PROJETO"
			value="<%= String.valueOf(TipoAtividadeExtensao.PROJETO) %>"
			scope="application" />
		<c:set var="PROGRAMA"
			value="<%= String.valueOf(TipoAtividadeExtensao.PROGRAMA) %>"
			scope="application" />
		<c:set var="PRODUTO"
			value="<%= String.valueOf(TipoAtividadeExtensao.PRODUTO) %>"
			scope="application" />
		<c:set var="CURSO"
			value="<%= String.valueOf(TipoAtividadeExtensao.CURSO) %>"
			scope="application" />
		<c:set var="EVENTO"
			value="<%= String.valueOf(TipoAtividadeExtensao.EVENTO) %>"
			scope="application" />
		<c:set var="PRESTACAO_SERVICO"
			value="<%= String.valueOf(TipoAtividadeExtensao.PRESTACAO_SERVICO) %>"
			scope="application" />

		<h3 class="tituloTabelaRelatorio">DADOS DA A��O DE EXTENS�O</h3>
		<table class="tabelaRelatorio" width="100%">
			<tbody>

				<%-- DADOS GERAIS, DE TODOS OS TIPOS DE A��O --%>

				<tr>
					<th width="23%"><b> C�digo: </b></th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.codigo}" /></td>
				</tr>

				<tr>
					<th><b> T�tulo: </b></th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.titulo}"/></td>
				</tr>

				<tr>
					<th><b> Ano: </b></th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.ano}" /></td>
				</tr>

				<tr>
					<th><b> Per�odo: </b></th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.dataInicio}" /> a
					<h:outputText value="#{atividadeExtensao.atividadeSelecionada.dataFim}" /></td>
				</tr>

				<tr>
					<th><b> Tipo: </b></th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.descricao}" /></td>
				</tr>

				<tr>
					<th><b> Situa��o: </b></th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.situacaoProjeto.descricao}" /></td>
				</tr>

				<c:if test="${(atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id != PROGRAMA) and (atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id != PRODUTO)}">
					<tr>
						<th><b> Munic�pio de Realiza��o: </b></th>
						<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.localRealizacao.municipioString}" escape="false" /></td>
					</tr>
					<tr>
						<th><b> Espa�o de Realiza��o: </b></th>
						<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.localRealizacao.descricao}" escape="false" /></td>
					</tr>
				</c:if>

				<tr>
					<th><b> Abrang�ncia: </b></th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.tipoRegiao.descricao}" /></td>
				</tr>

				<tr>
					<th><b> P�blico Alvo: </b></th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.publicoAlvo}" /></td>
				</tr>

				<tr>
					<th><b> Unidade Proponente:</b></th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.unidade.nome}" rendered="#{ not empty atividadeExtensao.atividadeSelecionada.unidade }"/> 
                        <h:outputText value=" / #{atividadeExtensao.atividadeSelecionada.unidade.gestora.sigla}" rendered="#{ not empty atividadeExtensao.atividadeSelecionada.unidade }"/>
                        <h:outputText value=" - " rendered="#{ empty atividadeExtensao.atividadeSelecionada.unidade }" />
                    </td>
				</tr>

                <tr>
                    <th><b> Unidade Or�ament�ria:</b></th>
                    <td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.projeto.unidadeOrcamentaria.nome}" rendered="#{ not empty atividadeExtensao.atividadeSelecionada.projeto.unidadeOrcamentaria }"/>  
                        <h:outputText value=" / #{atividadeExtensao.atividadeSelecionada.projeto.unidadeOrcamentaria.gestora.sigla}" rendered="#{ not empty atividadeExtensao.atividadeSelecionada.projeto.unidadeOrcamentaria }" />
                        <h:outputText value=" - " rendered="#{ empty atividadeExtensao.atividadeSelecionada.projeto.unidadeOrcamentaria }" />
                    </td>
                </tr>

				<tr>
					<th><b> Outras Unidades Envolvidas:</b></th>
					<td><t:dataTable id="unidadesEnvolvidas"
						value="#{atividadeExtensao.atividadeSelecionada.unidadesProponentes}"
						var="atividadeUnidade"
						rendered="#{not empty atividadeExtensao.atividadeSelecionada.unidadesProponentes}">

						<t:column>
							<h:outputText value="#{atividadeUnidade.unidade.nome}" />
							<f:verbatim> / </f:verbatim>
							<h:outputText value="#{atividadeUnidade.unidade.gestora.sigla}" />
						</t:column>

					</t:dataTable></td>
				</tr>

				<tr>
					<th><b> �rea Principal: </b></th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.areaTematicaPrincipal.descricao}" /></td>
				</tr>

				<tr>
					<th><b> �rea do CNPq:</b></th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.areaConhecimentoCnpq.nome}" /></td>
				</tr>
				
				<tr>
					<th><b>Fonte de Financiamento:</b></th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.fonteFinanciamentoString}" /></td>
				</tr>

				<tr>
					<th><b>Conv�nio Funpec:</b></th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.convenioFunpec ? 'SIM':'N�O'}" /></td>
				</tr>

				<tr>
					<th><b>Renova��o:</b></th>
					<td><h:outputText value="#{atividadeExtensao.obj.renovacao ? 'SIM':'N�O'}" /></td>
				</tr>
				
				<tr>
					<th><b>N� Bolsas Solicitadas:</b></th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.bolsasSolicitadas}" /></td>
				</tr>

				<tr>
					<th><b>N� Bolsas Concedidas:</b></th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.bolsasConcedidas}" /></td>
				</tr>

				<tr>
					<th><b>N� Discentes Envolvidos:</b></th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.totalDiscentes}" /></td>
				</tr>


				<c:if test="${(atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id != PROGRAMA)}">
					<tr>
						<th><b> Faz parte de Programa de Extens�o:</b></th>
						<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.vinculoProgramaExtensao ? 'SIM':'N�O'}" /></td>
					</tr>
				</c:if>



				<%-- S� PROJETO--%>
				<c:if
					test="${atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == PROJETO}">
					<tr>
						<th><b> Grupo Permanente de Arte e Cultura: </b></th>
						<td><h:outputText
							value="#{atividadeExtensao.atividadeSelecionada.permanente?'SIM': 'N�O'}" />
						</td>
					</tr>
				</c:if>


				<tr>
					<th><b>P�blico Estimado:</b></th>
					<td>
					   <h:outputText value="#{atividadeExtensao.atividadeSelecionada.publicoEstimado} pessoas" rendered="#{atividadeExtensao.atividadeSelecionada.publicoEstimado > 0 }" /> 
					   <h:outputText value="N�o informado" rendered="#{empty atividadeExtensao.atividadeSelecionada.publicoEstimado || atividadeExtensao.atividadeSelecionada.publicoEstimado == 0}" /> 
					</td>
				</tr>

				<tr>
					<th><b>P�blico Real Atendido:</b></th>
					<td>
    					<h:outputText value="#{atividadeExtensao.atividadeSelecionada.publicoAtendido} pessoas" rendered="#{atividadeExtensao.atividadeSelecionada.publicoAtendido > 0 }" /> 
                        <h:outputText value="N�o informado" rendered="#{empty atividadeExtensao.atividadeSelecionada.publicoAtendido || atividadeExtensao.atividadeSelecionada.publicoAtendido == 0}" /> 
					</td>
				</tr>


				<tr>
					<th width="23%"><b> Tipo de Cadastro: </b></th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.registro ? 'REGISTRO DE A��O REALIZADA' : 'SUBMISS�O DE NOVA PROPOSTA'}" /></td>
				</tr>



				<%-- 	DADOS ESPECIFICOS DE CURSO/EVENTO	--%>
				<c:if test="${(atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == CURSO) or (atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == EVENTO)}">

					<!-- A��O EH UM CURSO -->
					<c:if test="${atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == CURSO}">
						<tr>
							<th><b> Modalidade do Curso: </b></th>
							<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.cursoEventoExtensao.modalidadeEducacao.descricao}" /></td>
						</tr>
						<tr>
							<th><b> Tipo do Curso:</b></th>
							<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.cursoEventoExtensao.tipoCursoEvento.descricao}" /></td>
						</tr>
					</c:if>

					<!-- A��O EH UM EVENTO -->
					<c:if test="${atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == EVENTO}">
						<tr>
							<th><b> Tipo do Evento: </b></th>
							<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.cursoEventoExtensao.tipoCursoEvento.descricao}" /></td>
						</tr>
					</c:if>

					<tr>
						<th><b> Carga Hor�ria: </b></th>
						<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.cursoEventoExtensao.cargaHoraria}" />	horas</td>
					</tr>

					<tr>
						<th><b> Previs�o de N� de Vagas: </b></th>
						<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.cursoEventoExtensao.numeroVagas}" /></td>
					</tr>

				</c:if>

				<%-- 	DADOS ESPECIFICOS DE PRODUTO (tipoAtividade = 4)	--%>
				<c:if test="${atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == PRODUTO}">
					<tr>
						<th><b>Tipo do Produto: </b></th>
						<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.produtoExtensao.tipoProduto.descricao}" /></td>
					</tr>

					<tr>
						<th><b>Tiragem:</b></th>
						<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.produtoExtensao.tiragem}" /> exemplares</td>
					</tr>
				</c:if>




				<tr>
					<td colspan="2">
					<h3	style="text-align: center; background: #EFF3FA; font-size: 12px">Contato</h3>
					</td>
				</tr>

				<tr>
					<th><b>Coordena��o:</b></th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.coordenacao.pessoa.nome}"	rendered="#{(not empty atividadeExtensao.atividadeSelecionada.coordenacao)}"/></td>
				</tr>


				<tr>
					<th><b>E-mail:</b></th>
					<td>
						<a href="mailto:<h:outputText value='#{atividadeExtensao.atividadeSelecionada.coordenacao.pessoa.email}'/>">
							<h:outputText value="#{atividadeExtensao.atividadeSelecionada.coordenacao.pessoa.email}"
								rendered="#{(not empty atividadeExtensao.atividadeSelecionada.coordenacao.pessoa)}" />
						</a>
					</td>
				</tr>

				<tr>
					<th><b>Telefone:</b></th>
					<td>
					<h:outputText value="#{atividadeExtensao.atividadeSelecionada.coordenacao.pessoa.telefone}"
						rendered="#{(not empty atividadeExtensao.atividadeSelecionada.coordenacao.pessoa) && (acesso.extensao)}" />
					</td>
				</tr>


				<tr>
					<td colspan="2">
						<h3	style="text-align: center; background: #EFF3FA; font-size: 12px">Detalhes da A��o</h3>
					</td>
				</tr>


				<c:if test="${(atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == PROJETO) or (atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == PRODUTO) }">
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Justificativa: </b><br />
						<h:outputText value="#{atividadeExtensao.atividadeSelecionada.justificativa}" escape="false" /></td>
					</tr>
				</c:if>

				<c:if test="${(atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == PROJETO) }">
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Resumo: </b><br />
						<h:outputText value="#{atividadeExtensao.atividadeSelecionada.resumo}" escape="false" /></td>
					</tr>

					<tr>
						<td colspan="2" style="text-align: justify;"><b> Metodologia: </b><br />
						<h:outputText value="#{atividadeExtensao.atividadeSelecionada.metodologia}" escape="false" /></td>
					</tr>

					<tr>
						<td colspan="2" style="text-align: justify;"><b> Refer�ncias: </b><br />
						<h:outputText value="#{atividadeExtensao.atividadeSelecionada.referencias}" escape="false" /></td>
					</tr>
				</c:if>

				<%-- 	DADOS ESPECIFICOS DE CURSO/EVENTO	--%>
				<c:if test="${(atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == CURSO) or (atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == EVENTO)}">
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Resumo: </b><br />
						<h:outputText value="#{atividadeExtensao.atividadeSelecionada.cursoEventoExtensao.resumo}" escape="false" /></td>
					</tr>
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Programa��o: </b><br />
						<h:outputText value="#{atividadeExtensao.atividadeSelecionada.cursoEventoExtensao.programacao}"	escape="false" /></td>
					</tr>
				</c:if>

				<%-- 	DADOS ESPECIFICOS DE PROGRAMA (tipoAtividade = 3)	--%>
				<c:if test="${atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == PROGRAMA}">
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Resumo: </b><br />
						<h:outputText value="#{atividadeExtensao.atividadeSelecionada.resumo}" escape="false" /></td>
					</tr>
				</c:if>

				<%-- 	LISTAS GERAIS, DE A��o 	--%>
				<tr>
					<td colspan="2">
					<h3	style="text-align: center; background: #EFF3FA; font-size: 12px">Membros da Equipe</h3>
						<t:dataTable
							value="#{atividadeExtensao.atividadeSelecionada.membrosEquipe}"
							var="membro" align="center" width="100%" styleClass="listagem"
							rowClasses="linhaPar, linhaImpar" id="tbEquipe">
							<t:column>
								<f:facet name="header">
									<f:verbatim>Nome</f:verbatim>
								</f:facet>
								<h:outputText value="#{membro.pessoa.nome}" />
							</t:column>

							<t:column>
								<f:facet name="header">
									<f:verbatim>Categoria</f:verbatim>
								</f:facet>
								<h:outputText value="#{membro.categoriaMembro.descricao}" />
							</t:column>

							<t:column>
								<f:facet name="header">
									<f:verbatim>Fun��o</f:verbatim>
								</f:facet>
								<h:outputText value="<font color='red'>"
									rendered="#{membro.coordenadorAtivo}"
									escape="false" />
								<h:outputText value="#{membro.funcaoMembro.descricao}"
									rendered="#{not empty membro.pessoa}" />
								<h:outputText value="</font>"
									rendered="#{membro.coordenadorAtivo}"
									escape="false" />
							</t:column>

							<t:column>
								<f:facet name="header">
									<f:verbatim>Departamento</f:verbatim>
								</f:facet>
								<h:outputText value="#{membro.servidor.unidade.sigla}"
									rendered="#{not empty membro.servidor}" />
							</t:column>

							<t:column>
								<f:facet name="header">
									<f:verbatim>In�cio</f:verbatim>
								</f:facet>
								<h:outputText value="#{membro.dataInicio}" />
							</t:column>

							<t:column>
								<f:facet name="header">
									<f:verbatim>Fim</f:verbatim>
								</f:facet>
								<h:outputText value="#{membro.dataFim}" />
							</t:column>

						</t:dataTable>
						<h:outputText value="<center><font color='red'>Membros da equipe ainda n�o foram cadastrados.</font></center>" rendered="#{(empty atividadeExtensao.atividadeSelecionada.membrosEquipe)}" escape="false"/>
					</td>
				</tr>


				<tr>
					<td colspan="2">
					<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Discentes com Planos de Trabalho</h3>
						<t:dataTable
							value="#{atividadeExtensao.atividadeSelecionada.discentesSelecionados}"
							var="dis" align="center" width="100%" styleClass="listagem"
							rowClasses="linhaPar, linhaImpar" id="tbDis">
							<t:column>
								<f:facet name="header">
									<f:verbatim>Nome</f:verbatim>
								</f:facet>
								<h:outputText
									value="#{dis.discente.matriculaNome}" />
							</t:column>

							<t:column>
								<f:facet name="header">
									<f:verbatim>V�nculo</f:verbatim>
								</f:facet>
								<h:outputText
									value="#{dis.tipoVinculo.descricao}" />
							</t:column>

							<t:column>
								<f:facet name="header">
									<f:verbatim>Situa��o</f:verbatim>
								</f:facet>
								<h:outputText
									value="#{dis.situacaoDiscenteExtensao.descricao}" />
							</t:column>

							<t:column>
								<f:facet name="header">
									<f:verbatim>In�cio</f:verbatim>
								</f:facet>
								<h:outputText value="#{dis.dataInicio}" />
							</t:column>

							<t:column>
								<f:facet name="header">
									<f:verbatim>Fim</f:verbatim>
								</f:facet>
								<h:outputText value="#{dis.dataFim}" />
							</t:column>
						</t:dataTable>
						<h:outputText value="<center><font color='red'>Discentes n�o informados</font></center>" rendered="#{(empty atividadeExtensao.atividadeSelecionada.discentesSelecionados)}" escape="false"/>
					</td>
				</tr>

				<tr>
					<td colspan="2">
					<h3	style="text-align: center; background: #EFF3FA; font-size: 12px">A��es Vinculadas ao <h:outputText
						value="#{atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.descricao}" /></h3>
					<t:dataTable
						value="#{atividadeExtensao.atividadeSelecionada.atividades}"
						var="atividade" align="center" width="100%" styleClass="listagem"
						rowClasses="linhaPar, linhaImpar" id="tbAtividades">
						<t:column>
							<f:facet name="header">
								<f:verbatim>C�digo - T�tulo</f:verbatim>
							</f:facet>
							<h:outputText value="#{atividade.codigoTitulo}" />
						</t:column>

						<t:column width="10%">
							<f:facet name="header">
								<f:verbatim>Tipo</f:verbatim>
							</f:facet>
							<h:outputText
								value="#{atividade.tipoAtividadeExtensao.descricao}" />
						</t:column>
					</t:dataTable> 
					<h:outputText value="<center><font color='red'>N�o h� a��es vinculadas</font></center>" rendered="#{(empty atividadeExtensao.atividadeSelecionada.atividades)}" escape="false"/>
					</td>
				</tr>

				<tr>
					<td colspan="2">
					<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">A��es das quais o <h:outputText
						value="#{atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.descricao}" /> faz parte</h3>
					<t:dataTable
						value="#{atividadeExtensao.atividadeSelecionada.atividadesPai}"
						var="atividade" align="center" width="100%" styleClass="listagem"
						rowClasses="linhaPar, linhaImpar" id="tbAtividadesPai">
						<t:column>
							<f:facet name="header">
								<f:verbatim>C�digo - T�tulo</f:verbatim>
							</f:facet>
							<h:outputText value="#{atividade.codigoTitulo}" />
						</t:column>

						<t:column width="10%">
							<f:facet name="header">
								<f:verbatim>Tipo</f:verbatim>
							</f:facet>
							<h:outputText
								value="#{atividade.tipoAtividadeExtensao.descricao}" />
						</t:column>
					</t:dataTable> 
					
					<c:if test="${atividadeExtensao.atividadeSelecionada.projetoAssociado}">
						<table align="center" width="100%" class="listagem" id="tbProjetoAssociado">
							<thead>
								<tr>
									<th>T�tulo</th>
									<th>Tipo</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.projeto.titulo}" /></td>
									<td>A��O ACAD�MICA ASSOCIADA</td>
								</tr>										
							</tbody>										
						</table>
					</c:if> 
								
					
					<c:if test="${(empty atividadeExtensao.atividadeSelecionada.atividadesPai) and (not atividadeExtensao.atividadeSelecionada.projetoAssociado)}">
						<center><font color="red">Esta a��o n�o faz parte
						de outros projetos ou programas de extens�o</font></center>
					</c:if></td>
				</tr>




				<%-- 	LISTAS EXPECIFICAS DE PROJETO (tipoAtividade = 1)	--%>
				<c:if
					test="${atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == PROJETO}">


					<!-- OBJETIVOS / RESULTADOS ESPERADOS -->
					<c:if
						test="${not empty atividadeExtensao.atividadeSelecionada.objetivo}">
						<tr>
							<td colspan="2">
							<h3
								style="text-align: center; background: #EFF3FA; font-size: 12px">Objetivos
							/ Resultados Esperados</h3>

							<t:dataTable
								value="#{atividadeExtensao.atividadeSelecionada.objetivo}"
								var="objetivo" align="center" width="100%" styleClass="listagem"
								rowClasses="linhaPar, linhaImpar" id="tbObjetivos" >
								<t:column>
									<f:facet name="header">
										<f:verbatim>Objetivos Gerais</f:verbatim>
									</f:facet>
									<f:verbatim><div align="justify"><h:outputText value="#{objetivo.objetivo}" escape="false" /></div></f:verbatim>
								</t:column>
								<t:column>
									<f:facet name="header">
										<f:verbatim>Quantitativos</f:verbatim>
									</f:facet>									
									<f:verbatim><div align="justify"><h:outputText value="#{objetivo.quantitativos}" escape="false" /></div></f:verbatim>
								</t:column>
								<t:column>
									<f:facet name="header">
										<f:verbatim>Qualitativos</f:verbatim>
									</f:facet>
									<f:verbatim><div align="justify"><h:outputText value="#{objetivo.qualitativos}" escape="false" /></div></f:verbatim>
								</t:column>
							</t:dataTable></td>
						</tr>
					</c:if>


					<!-- CRONOGRAMA -->
					<c:if
						test="${not empty atividadeExtensao.atividadeSelecionada.objetivo}">
						<tr>
							<td colspan="2">
							<h3
								style="text-align: center; background: #EFF3FA; font-size: 12px">Cronograma</h3>
							<table class="formulario" width="100%">
								<thead>
									<tr>
										<th width="70%">Descri��o das ativadades desenvolvidas</th>
										<th>Per�odo</th>
									</tr>
								</thead>

								<tbody>
									<c:forEach
										items="#{atividadeExtensao.atividadeSelecionada.objetivo}"
										var="objet" varStatus="status1">
										<c:forEach items="#{objet.atividadesPrincipais}" var="ativ"
											varStatus="status2">
											<tr class="${(status1.index + status2.index ) % 2 == 0 ? "linhaPar" : "linhaImpar" }">
												<td align="justify"><h:outputText
													value="#{ativ.descricao}" escape="false" /></td>
												<td><fmt:formatDate value="${ativ.dataInicio}"
													pattern="dd/MM/yyyy" /> <c:if
													test="${not empty ativ.dataFim}">
																&nbsp; a &nbsp; 
															</c:if> <fmt:formatDate value="${ativ.dataFim}"
													pattern="dd/MM/yyyy" /></td>
											</tr>
										</c:forEach>
									</c:forEach>
								</tbody>


							</table>
							</td>
						</tr>
					</c:if>

				</c:if>



				<!-- OR�AMENTO DETALHADO -->
				<c:if
					test="${not empty atividadeExtensao.atividadeSelecionada.orcamentosDetalhados}">
					<tr>
						<td colspan="2">
						<h3
							style="text-align: center; background: #EFF3FA; font-size: 12px">Or�amento
						Detalhado</h3>
						<table class="listagem">
							<thead>
								<tr>
									<th>Descri��o</th>
									<th style="text-align: right" width="15%">Valor Unit�rio</th>
									<th style="text-align: right" width="10%">Quant.</th>
									<th style="text-align: right" width="15%">Valor Total</th>
								</tr>
							</thead>

							<tbody>

								<c:if test="${not empty atividadeExtensao.tabelaOrcamentaria}">

									<c:set value="${atividadeExtensao.tabelaOrcamentaria}"
										var="tabelaOrcamentaria" />
									<c:forEach items="#{tabelaOrcamentaria}" var="tabelaOrc">

										<tr
											style="background: #EFF0FF; font-weight: bold; padding: 2px 0 2px 5px;">
											<td colspan="5">${ tabelaOrc.key.descricao }</td>
										</tr>
										<c:set value="#{tabelaOrc.value.orcamentos}" var="orcamentos" />
										<c:forEach items="#{orcamentos}" var="orcamento"
											varStatus="status">
											<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
												<td style="padding-left: 20px">
												${orcamento.discriminacao}</td>
												<td align="right"><fmt:formatNumber
													currencySymbol="R$  " value="${orcamento.valorUnitario}"
													type="currency" /></td>
												<td align="right">${orcamento.quantidade}</td>
												<td align="right"><fmt:formatNumber
													currencySymbol="R$  " value="${orcamento.valorTotal}"
													type="currency" /></td>
											</tr>
										</c:forEach>

										<tr style="background: #EEE; padding: 2px 0 2px 5px;">
											<td colspan="2">SUB-TOTAL (${ tabelaOrc.key.descricao})</td>
											<td align="right">${ tabelaOrc.value.quantidadeTotal }</td>
											<td align="right"><fmt:formatNumber
												currencySymbol="R$  "
												value="${ tabelaOrc.value.valorTotalRubrica }"
												type="currency" /></td>
										</tr>

										<tr>
											<td colspan="5">&nbsp;</td>
										</tr>

									</c:forEach>
								</c:if>

								<c:if
									test="${empty atividadeExtensao.atividadeSelecionada.orcamentosDetalhados}">
									<tr>
										<td colspan="6" align="center"><font color="red">N�o
										h� itens de despesas cadastrados</font></td>
									</tr>
								</c:if>

							</tbody>
						</table>

						</td>
					</tr>
				</c:if>


				<!-- OR�AMENTO CONSOLIDADO -->
				<c:if
					test="${not empty atividadeExtensao.atividadeSelecionada.orcamentosConsolidados}">
					<tr>
						<td colspan="2">
						<h3
							style="text-align: center; background: #EFF3FA; font-size: 12px">Consolida��o
						do Orcamento Solicitado</h3>

						<t:dataTable id="dt"
							value="#{atividadeExtensao.atividadeSelecionada.orcamentosConsolidados}"
							var="consolidacao" align="center" width="100%"
							styleClass="listagem" rowClasses="linhaPar, linhaImpar"
							rowIndexVar="index" forceIdIndex="true">
							<t:column>
								<f:facet name="header">
									<f:verbatim>Descri��o</f:verbatim>
								</f:facet>
								<h:outputText value="#{consolidacao.elementoDespesa.descricao}" />
							</t:column>

							<t:column>
								<f:facet name="header">
									<f:verbatim>FAEx (Interno)</f:verbatim>
								</f:facet>
								<h:outputText value="#{consolidacao.fundo}">
									<f:convertNumber pattern="R$ #,##0.00" />
								</h:outputText>
							</t:column>

							<t:column>
								<f:facet name="header">
									<f:verbatim>Funpec</f:verbatim>
								</f:facet>
								<h:outputText value="#{consolidacao.fundacao}">
									<f:convertNumber pattern="R$ #,##0.00" />
								</h:outputText>
							</t:column>

							<t:column>
								<f:facet name="header">
									<f:verbatim>Outros (Externo)</f:verbatim>
								</f:facet>
								<h:outputText value="#{consolidacao.outros}">
									<f:convertNumber pattern="R$ #,##0.00" />
								</h:outputText>
							</t:column>

							<t:column style="text-align: right">
								<f:facet name="header">
									<f:verbatim>Total Rubrica</f:verbatim>
								</f:facet>
								<h:outputText value="#{consolidacao.totalConsolidado}">
									<f:convertNumber pattern="R$ #,##0.00" />
								</h:outputText>
							</t:column>
						</t:dataTable></td>
					</tr>
				</c:if>

				<c:if
					test="${not empty atividadeExtensao.atividadeSelecionada.arquivos}">
					<tr>
						<td colspan="2">
						<h3
							style="text-align: center; background: #EFF3FA; font-size: 12px">Arquivos</h3>
						<t:dataTable
							value="#{atividadeExtensao.atividadeSelecionada.arquivos}"
							var="arquivo" align="center" width="100%" styleClass="listagem"
							rowClasses="linhaPar, linhaImpar" id="tbArquivo">
							<t:column>
								<f:facet name="header">
									<f:verbatim>Descri��o Arquivo</f:verbatim>
								</f:facet>
								<h:outputText value="#{arquivo.descricao}" />
							</t:column>
						</t:dataTable></td>
					</tr>
				</c:if>

				<!-- OR�AMENTO APROVADO  -->
				<c:if test="${not empty atividadeExtensao.atividadeSelecionada.orcamentosConsolidados}">
						<tr>
							<td colspan="2">
								<a name="orcamento_aprovado" />
								<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Or�amento Aprovado</h3>

								<t:dataTable id="dt_aprovado"
									value="#{atividadeExtensao.atividadeSelecionada.orcamentosConsolidados}"
									var="consolidacao" align="center" width="100%"
									styleClass="listagem" rowClasses="linhaPar, linhaImpar"
									rowIndexVar="index" forceIdIndex="true">
									<t:column>
										<f:facet name="header">
											<f:verbatim>Descri��o</f:verbatim>
										</f:facet>
										<h:outputText value="#{consolidacao.elementoDespesa.descricao}" />
									</t:column>
	
									<t:column>
										<f:facet name="header">
											<f:verbatim>FAEx (Interno)</f:verbatim>
										</f:facet>
										<h:outputText value="#{consolidacao.fundoConcedido}">
											<f:convertNumber pattern="R$ #,##0.00" />
										</h:outputText>
									</t:column>
								</t:dataTable>
							</td>
						</tr>
				</c:if>

				<c:if test="${not empty atividadeExtensao.atividadeSelecionada.autorizacoesDepartamentos}">
					<tr>
						<td colspan="2">
						<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Lista de departamentos envolvidos na autoriza��o da proposta</h3>
						<t:dataTable
							value="#{atividadeExtensao.atividadeSelecionada.autorizacoesDepartamentos}"
							var="autorizacao" align="center" width="100%"
							styleClass="listagem" rowClasses="linhaPar, linhaImpar"
							id="tbAutorizacao">
							<t:column rendered="#{autorizacao.ativo}">
								<f:facet name="header">
									<f:verbatim>Autoriza��o</f:verbatim>
								</f:facet>
								<h:outputText value="#{autorizacao.unidade.nome}" />
							</t:column>
							<t:column rendered="#{autorizacao.ativo}">
								<f:facet name="header">
									<f:verbatim>Data An�lise</f:verbatim>
								</f:facet>
								<h:outputText value="#{autorizacao.dataAutorizacao}">
									<f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss" />
								</h:outputText>
							</t:column>
							<t:column rendered="#{autorizacao.ativo}">
								<f:facet name="header">
									<f:verbatim>Autorizado</f:verbatim>
								</f:facet>
								<h:outputText value="#{(autorizacao.autorizado == null) ? 'N�O ANALISADO' :(autorizacao.autorizado ? 'SIM': 'N�O')}" />
							</t:column>
						</t:dataTable></td>
					</tr>
				</c:if>

			</tbody>		

		</table>


	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>