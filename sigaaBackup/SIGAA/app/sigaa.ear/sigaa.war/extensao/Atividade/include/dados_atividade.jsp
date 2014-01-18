<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>

<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.FuncaoMembro"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>

<c:set var="COORDENADOR" 			value="<%= String.valueOf(FuncaoMembro.COORDENADOR) %>" 				scope="application"/>
<c:set var="PROJETO" 				value="<%= String.valueOf(TipoAtividadeExtensao.PROJETO) %>" 			scope="application"/>
<c:set var="PROGRAMA" 				value="<%= String.valueOf(TipoAtividadeExtensao.PROGRAMA) %>" 			scope="application"/>
<c:set var="PRODUTO" 				value="<%= String.valueOf(TipoAtividadeExtensao.PRODUTO) %>" 			scope="application"/>
<c:set var="CURSO" 					value="<%= String.valueOf(TipoAtividadeExtensao.CURSO) %>" 				scope="application"/>
<c:set var="EVENTO"					value="<%= String.valueOf(TipoAtividadeExtensao.EVENTO) %>" 			scope="application"/>
<c:set var="PRESTACAO_SERVICO" 		value="<%= String.valueOf(TipoAtividadeExtensao.PRESTACAO_SERVICO) %>" 	scope="application"/>

<tbody>
		
		<%-- DADOS GERAIS, DE TODOS OS TIPOS DE AÇÂO --%>

		<tr>
			<th width="23%"><b> Código: </b> </th>
			<td><h:outputText value="#{atividadeExtensao.obj.codigo}"/></td>
		</tr>
	
		<tr>
			<th><b> Título: </b> </th>
			<td> 
				<h:outputText value="#{atividadeExtensao.obj.titulo}">
					<f:attribute name="lineWrap" value="100"/>
					<f:converter converterId="convertTexto" />
				</h:outputText>
			</td>
		</tr>

		<tr>
			<th><b> Ano: </b> </th>
			<td><h:outputText value="#{atividadeExtensao.obj.ano}"/></td>
		</tr>

		<tr>
			<th><b> Período: </b> </th>
			<td><h:outputText value="#{atividadeExtensao.obj.dataInicio}"/> a <h:outputText value="#{atividadeExtensao.obj.dataFim}"/></td>
		</tr>
	
		<tr>
			<th><b> Tipo: </b> </th>
			<td> <h:outputText value="#{atividadeExtensao.obj.tipoAtividadeExtensao.descricao}"/> </td>
		</tr>
	
	
		<tr>
			<th><b> Situação: </b> </th>
			<td> <h:outputText value="#{atividadeExtensao.obj.situacaoProjeto.descricao}"/> </td>
		</tr>
	
		<c:if test="${(atividadeExtensao.obj.tipoAtividadeExtensao.id != PROGRAMA) and (atividadeExtensao.obj.tipoAtividadeExtensao.id != PRODUTO)}"> 

				<tr>
					<th><b> Local de Realização: </b></th>
					<td>
						<h:panelGroup id="locaisRealizacao" rendered="#{ not empty atividadeExtensao.locaisRealizacao }">
							<t:dataTable id="realizacao" value="#{ atividadeExtensao.locaisRealizacao }"
								var="local" align="center" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
								<t:column>
									<f:facet name="header">
										<f:verbatim>Estado</f:verbatim>
									</f:facet>
									<h:outputText value="#{ local.municipio.unidadeFederativa.descricao }" />
								</t:column>
			
								<t:column>
									<f:facet name="header">
										<f:verbatim>Município</f:verbatim>
									</f:facet>
									<h:outputText value="#{ local.municipio.nome }" />
								</t:column>
	
								<t:column>
									<f:facet name="header">
										<f:verbatim>Bairro</f:verbatim>
									</f:facet>
									<h:outputText value="#{ local.bairro }" />
								</t:column>

								<t:column>
									<f:facet name="header">
										<f:verbatim>Espaço de Realização</f:verbatim>
									</f:facet>
									<h:outputText value="#{ local.descricao }" />
								</t:column>
							</t:dataTable>
						</h:panelGroup>
					
					</td>
				</tr>
				
		</c:if>
		
		<tr>
			<th><b> Abrangência: </b> </th>
			<td> <h:outputText value="#{atividadeExtensao.obj.tipoRegiao.descricao}"/> </td>
		</tr>

		<tr>
			<th><b> Público Alvo Interno: </b> </th>
			<td> 
				<h:outputText value="#{atividadeExtensao.obj.publicoAlvo}" >
					<f:attribute name="lineWrap" value="100"/>
					<f:converter converterId="convertTexto" />
				</h:outputText>
			</td>
		</tr>
		
		<tr>
			<th><b> Público Alvo Externo: </b> </th>
			<td> 
				<h:outputText value="#{atividadeExtensao.obj.publicoAlvoExterno}" >
					<f:attribute name="lineWrap" value="100"/>
					<f:converter converterId="convertTexto" />
				</h:outputText>
			</td>
		</tr>
		
		<tr>
			<th><b> Unidade Proponente:</b> </th>
			<td> <h:outputText value="#{atividadeExtensao.obj.projeto.coordenador.servidor.unidade.nome}"/> </td>
			
		</tr>
		
		<tr>
			<th><b> Executor Financeiro:</b> </th>
			<td> 
				<h:outputText value="#{atividadeExtensao.executorFinanceiro}" rendered="#{ not empty atividadeExtensao.obj.executorFinanceiro}"/>
			</td>
		</tr>

		<tr>
			<th><b> Unidade Co-Executora Externa:</b> </th>
			<td> 
				<h:outputText value="#{atividadeExtensao.obj.unidadeExecutoraExterna}" rendered="#{ not empty atividadeExtensao.obj.unidadeExecutoraExterna}"/>
			</td>
		</tr>
		
		<tr>
			<th><b> Unidades Envolvidas:</b> </th>
			<td> 
				<t:dataTable id="unidadesEnvolvidas" value="#{atividadeExtensao.obj.unidadesProponentes}" var="atividadeUnidade" rendered="#{not empty atividadeExtensao.obj.unidadesProponentes}">
					<t:column>
						<h:outputText value="#{atividadeUnidade.unidade.nome}"/>
						<h:outputText value=" / #{atividadeUnidade.unidade.gestora.sigla}" rendered="#{ not empty atividadeUnidade.unidade }" />
					</t:column>
				</t:dataTable>
			</td>
		</tr>
		
		<tr>
			<th><b> Área Principal: </b> </th>
			<td> <h:outputText value="#{atividadeExtensao.obj.areaTematicaPrincipal.descricao}"/> </td>
		</tr>
	
		<tr>
			<th><b> Área do CNPq:</b> </th>
			<td> <h:outputText value="#{atividadeExtensao.obj.areaConhecimentoCnpq.nome}"/> </td>
		</tr>
		
		<tr>
			<th><b>Fonte de Financiamento:</b></th>
			<td>
				<h:outputText value="#{atividadeExtensao.obj.fonteFinanciamentoString}"/>
			</td>
		</tr>
		
		<tr>
			<th><b>Linha de Atuação:</b></th>
			<td>
				<h:outputText value="#{atividadeExtensao.obj.linhaAtuacao.descricao}"/>
			</td>
		</tr>
		
		<tr>
			<th><b>Convênio Funpec:</b></th>
			<td>
				<h:outputText value="#{atividadeExtensao.obj.convenioFunpec ? 'SIM':'NÃO'}" />
			</td>
		</tr>

        <tr>
            <th><b>Possui Financiamento Externo nos Termos do Edital?</b></th>
            <td>
                <h:outputText value="#{atividadeExtensao.obj.financExternoEdital? 'SIM':'NÃO'}" />
            </td>
        </tr>

		
		<tr>
			<th><b>Nº Bolsas Solicitadas:</b></th>
			<td>
				<h:outputText value="#{atividadeExtensao.obj.bolsasSolicitadas}"/>
			</td>
		</tr>
		
		<c:if test="${atividadeExtensao.obj.situacaoProjeto.id eq APROVADO}">
			<tr>
				<th><b>Nº Bolsas Concedidas:</b></th>
				<td>
					<h:outputText value="#{atividadeExtensao.obj.bolsasConcedidas}"/>
				</td>
			</tr>
		</c:if>

		<tr>
			<th><b>Nº Discentes Envolvidos:</b></th>
			<td>
				<h:outputText value="#{atividadeExtensao.obj.totalDiscentes}"/>
			</td>
		</tr>


	<c:if test="${(atividadeExtensao.obj.tipoAtividadeExtensao.id != PROGRAMA)}">
		<tr>
			<th><b> Faz parte de Programa de Extensão:</b></th>
			<td><h:outputText
					value="#{atividadeExtensao.obj.vinculoProgramaExtensao ? 'SIM':'NÃO'}" />
			</td>
		</tr>
		<tr>
			<th><b> Faz parte de Programa Estratégico de Extensão:</b></th>
			<td><h:outputText value="#{atividadeExtensao.obj.vinculoProgramaEstrategico ? 'SIM':'NÃO'}" />
				
				<h:outputText value=" (#{atividadeExtensao.programaEstrategico })"
					rendered="#{atividadeExtensao.obj.vinculoProgramaEstrategico }"/>
			</td>
		</tr>
	</c:if>



	<%-- SÓ PROJETO--%>
	  <c:if test="${atividadeExtensao.obj.tipoAtividadeExtensao.id == PROJETO}">
  		<tr>
			<th><b> Grupo Permanente de Arte e Cultura: </b></th>
			<td>
				<h:outputText value="#{atividadeExtensao.obj.permanente?'SIM': 'NÃO'}" />
			</td>
		</tr>
	 </c:if>


		<tr>
			<th><b>Público Estimado Interno:</b></th>
			<td>
				<h:outputText value="#{atividadeExtensao.obj.publicoEstimado}"/>
			</td>
		</tr>
		
		<tr>
			<th><b>Público Estimado Externo:</b></th>
			<td>
				<h:outputText value="#{atividadeExtensao.obj.publicoExterno}"/>
			</td>
		</tr>

		
		<tr>
			<th width="23%"><b> Tipo de Cadastro: </b> </th>
			<td> <h:outputText value="#{atividadeExtensao.obj.registro ? 'REGISTRO' : 'SUBMISSÃO DE PROPOSTA'}"/> </td>
		</tr>



		<%-- 	DADOS ESPECIFICOS DE CURSO/EVENTO	--%>
		<c:if test="${(atividadeExtensao.obj.tipoAtividadeExtensao.id == CURSO) or (atividadeExtensao.obj.tipoAtividadeExtensao.id == EVENTO)}"> 
	
			<!-- AÇÃO EH UM CURSO -->
			<c:if test="${atividadeExtensao.obj.tipoAtividadeExtensao.id == CURSO}"> 
				<tr>
					<th><b> Modalidade do Curso: </b> </th>
					<td> <h:outputText value="#{atividadeExtensao.obj.cursoEventoExtensao.modalidadeEducacao.descricao}"/> </td>
				</tr>				
		
				<tr>
					<th><b> Tipo do Curso:</b> </th>
					<td> <h:outputText value="#{atividadeExtensao.obj.cursoEventoExtensao.tipoCursoEvento.descricao}"/> </td>
				</tr>				
			</c:if>
	
			<!-- AÇÃO EH UM EVENTO -->
			<c:if test="${atividadeExtensao.obj.tipoAtividadeExtensao.id == EVENTO}"> 
				<tr>
					<th><b> Tipo do Evento: </b></th>
					<td> <h:outputText value="#{atividadeExtensao.obj.cursoEventoExtensao.tipoCursoEvento.descricao}"/> </td>
				</tr>				
			</c:if>
	
			<tr>
				<th><b> Carga Horária: </b></th>
				<td> <h:outputText value="#{atividadeExtensao.obj.cursoEventoExtensao.cargaHoraria}"/> horas</td>
			</tr>				
	
			<tr>
				<th><b> Previsão de Nº de Vagas: </b> </th>
				<td> <h:outputText value="#{atividadeExtensao.obj.cursoEventoExtensao.numeroVagas}"/> </td>
			</tr>				

		</c:if>

		<%-- 	DADOS ESPECIFICOS DE PRODUTO (tipoAtividade = 4)	--%>
		<c:if test="${atividadeExtensao.obj.tipoAtividadeExtensao.id == PRODUTO}"> 
	
			<tr>
				<th> <b>Tipo do Produto: </b></th>
				<td><h:outputText value="#{atividadeExtensao.obj.produtoExtensao.tipoProduto.descricao}"/></td>
			</tr>	
			
			<tr>
				<th><b>Tiragem:</b></th>
				<td>
					<h:outputText value="#{atividadeExtensao.obj.produtoExtensao.tiragem}"/> exemplares
				</td>
			</tr>
	
		</c:if>


		<tr>
			<td colspan="2"><h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Detalhes da Ação</h3></td>
		</tr>
			
			
		<c:if test="${atividadeExtensao.obj.tipoAtividadeExtensao.id != PRODUTO}">
			<tr>
				<td colspan="2" align="justify">
					<b> Resumo:  </b><br/>
					<c:if test="${(atividadeExtensao.obj.tipoAtividadeExtensao.id != CURSO) and (atividadeExtensao.obj.tipoAtividadeExtensao.id != EVENTO)}">
						<h:outputText value="#{atividadeExtensao.obj.resumo}" escape="false"/>
					</c:if>					
					<c:if test="${(atividadeExtensao.obj.tipoAtividadeExtensao.id == CURSO) or (atividadeExtensao.obj.tipoAtividadeExtensao.id == EVENTO)}">
						<h:outputText value="#{atividadeExtensao.obj.cursoEventoExtensao.resumo}" escape="false"/> </td>
					</c:if>
				</td>
			</tr>
		</c:if>
		
		<c:if test="${atividadeExtensao.obj.tipoAtividadeExtensao.id == PROGRAMA}">
			<tr>
				<td colspan="2" > 
					<b> Justificativa para execução do projeto: </b><br/>
					<h:outputText value="#{atividadeExtensao.obj.justificativa}" escape="false"/> 
				</td>
			</tr>
			
			<tr>
				<td colspan="2"> 
					<b> Metodologia de desenvolvimento do projeto: </b><br/>
					<h:outputText value="#{ atividadeExtensao.obj.fundamentacaoTeorica }" escape="false"/> 
				</td>
			</tr>
			
			<tr>
				<td colspan="2"> 
					<b> Referências:  </b><br/>
					<h:outputText value="#{ atividadeExtensao.obj.projeto.referencias }" escape="false"/> 
				</td>
			</tr>
			
			<tr>
				<td colspan="2"> 
					<b> Objetivos Gerais:  </b><br/>
					<h:outputText value="#{ atividadeExtensao.obj.projeto.objetivos }" escape="false"/> 
				</td>
			</tr>
			
			<tr>
				<td colspan="2"> 
					<b> Resultados Esperados:  </b><br/>
					<h:outputText value="#{ atividadeExtensao.obj.projeto.resultados }" escape="false"/> 
				</td>
			</tr>
		
		</c:if>

		
		<c:if test="${atividadeExtensao.obj.tipoAtividadeExtensao.id == PRODUTO }"> 
			<tr>
				<td colspan="2" align="justify"> 
					<b> Resumo: </b><br/>
					<h:outputText value="#{atividadeExtensao.obj.projeto.resumo}" escape="false"/> 
				</td>
			</tr>
			
		</c:if>	
		
		<c:if test="${(atividadeExtensao.obj.tipoAtividadeExtensao.id == PROJETO) or (atividadeExtensao.obj.tipoAtividadeExtensao.id == PRODUTO) }"> 

			<tr>
				<td colspan="2" align="justify"> 
					<b> Justificativa: </b><br/>
					<h:outputText value="#{atividadeExtensao.obj.justificativa}" escape="false"/> 
				</td>
			</tr>
			
		</c:if>	
		
		<c:if test="${atividadeExtensao.obj.tipoAtividadeExtensao.id == PRODUTO }"> 
			<tr>
				<td colspan="2" align="justify"> 
					<b> Objetivos Gerais: </b><br/>
					<h:outputText value="#{atividadeExtensao.obj.projeto.objetivos}" escape="false" rendered="#{ not empty atividadeExtensao.obj.projeto.objetivos }"/>
					<h:outputText value="Não Informado"  rendered="#{  empty atividadeExtensao.obj.projeto.objetivos }"/> 
				</td>
			</tr>
			
			<tr>
				<td colspan="2" align="justify"> 
					<b> Resultados Esperados: </b><br/>
					<h:outputText value="#{atividadeExtensao.obj.projeto.resultados }" escape="false" rendered="#{ not empty atividadeExtensao.obj.projeto.resultados  }"/>
					<h:outputText value="Não Informado" rendered="#{ empty atividadeExtensao.obj.projeto.resultados  }"/> 
				</td>
			</tr>
			
		</c:if>	
			
	
		<c:if test="${(atividadeExtensao.obj.tipoAtividadeExtensao.id == PROJETO) }">
			<tr>
				<td colspan="2" align="justify"> 
					<b> Fundação Teórica: </b><br/>
					<h:outputText value="#{atividadeExtensao.obj.fundamentacaoTeorica}" escape="false"/> 
				</td>
			</tr>
			
			<tr>
				<td colspan="2" align="justify"> 
					<b> Metodologia: </b><br/>
					<h:outputText value="#{atividadeExtensao.obj.metodologia}" escape="false"/> 
				</td>
			</tr>
			<tr>
				<td colspan="2" align="justify"> 
					<b> Referências: </b><br/>
					<h:outputText value="#{atividadeExtensao.obj.referencias}" escape="false"/> 
				</td>
			</tr>
			
			<tr>
				<td colspan="2" align="justify"> 
					<b> Objetivos Gerais: </b><br/>
					<h:outputText value="#{atividadeExtensao.obj.objetivos }" escape="false"/> 
				</td>
			</tr>
			
			<tr>
				<td colspan="2" align="justify">
					<b>Resultados Esperados</b><br/>
					<h:outputText value="#{atividadeExtensao.obj.projeto.resultados}" escape="false"/> 
				</td>
			</tr>
			
		</c:if>
		
		<%-- 	DADOS ESPECIFICOS DE CURSO/EVENTO	--%>
		<c:if test="${(atividadeExtensao.obj.tipoAtividadeExtensao.id == CURSO) or (atividadeExtensao.obj.tipoAtividadeExtensao.id == EVENTO)}"> 
			<tr>
				<td colspan="2" > 
					<b> Programação: </b><br/>
					<h:outputText value="#{atividadeExtensao.obj.cursoEventoExtensao.programacao}" escape="false"/>
				</td>
			</tr>
			
			<tr>
				 <td colspan="2" align="justify">
					<b> Objetivos Gerais: </b><br/>
					<h:outputText value="#{ atividadeExtensao.obj.projeto.objetivos }" escape="false" rendered="#{  not empty atividadeExtensao.obj.projeto.objetivos  }" />
					<h:outputText value=" --"  rendered="#{   empty atividadeExtensao.obj.projeto.objetivos  }" />
				</td>
			</tr>
								
			<tr>
				<td colspan="2" style="text-align: justify;">
					<b> Resultados Esperados:</b><br/>
					<h:outputText value="#{ atividadeExtensao.obj.projeto.resultados }" rendered="#{ not empty atividadeExtensao.obj.projeto.resultados }" escape="false" />
					<h:outputText value=" --" rendered="#{ empty atividadeExtensao.obj.projeto.resultados }" escape="false" />
				</td>
			</tr>
		
		
		</c:if>
		
		
		
			
		
	
		<%-- 	LISTAS GERAIS, DE Ação 	--%>
		<c:if test="${not empty atividadeExtensao.obj.membrosEquipe}">
			<tr>
				<td colspan="2">
				<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Membros da Equipe</h3>
					<t:dataTable value="#{atividadeExtensao.membrosEquipe}" var="membro" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" id="tbEquipe">
						<t:column>
							<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
							<h:outputText value="#{membro.pessoa.nome}" />
						</t:column>
	
						<t:column>
							<f:facet name="header"><f:verbatim>Categoria</f:verbatim></f:facet>
							<h:outputText value="#{membro.categoriaMembro.descricao}" />
						</t:column>										
						
						<t:column>
							<f:facet name="header"><f:verbatim>Função</f:verbatim></f:facet>
							<h:outputText value="<font color='red'>" rendered="#{membro.coordenador}"  escape="false"/>
							<h:outputText value="#{membro.funcaoMembro.descricao}" rendered="#{not empty membro.pessoa}" />
							<h:outputText value="</font>" rendered="#{membro.coordenador}"  escape="false"/>
						</t:column>										
						
						<t:column>
							<f:facet name="header"><f:verbatim>Departamento</f:verbatim></f:facet>
							<h:outputText value="#{membro.unidade.nome}" rendered="#{not empty membro.servidor}" />
						</t:column>
					</t:dataTable>
				</td>
			</tr>
		 </c:if>


				<tr>
					<td colspan="2">
					<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Objetivos Cadastrados</h3>
						<c:forEach items="#{ atividadeExtensao.objetivos }" var="objetivo" varStatus="status">
							<tbody>
								<tr>
									<td colspan="2" align="left">									
										<h3 style="text-align: center; background: #EFF3FA; font-size: 12px"><h:outputText value="#{ objetivo.objetivo }" /></h3>
									</td>
								</tr>
							</tbody>
							<tr>
								<td colspan="3">
									<table style="width: 100%;" id="tbAtividades">
										<tbody>
											<tr>
												<td colspan="2">									
													<b>Atividades Relacionadas:</b>
												</td>
												<td>									
													<b>Período Realização:</b>
												</td>
												<td style="text-align: center;">									
													<b>Carga Horária:</b>
												</td>
											</tr>
											
											<c:forEach items="#{objetivo.atividadesPrincipais}" var="atividade" varStatus="st1">
												<tr>
													<td colspan="2">
														${st1.index + 1}. <h:outputText value="#{atividade.descricao}" />
													</td>
													<td>	
														<h:outputText value="#{atividade.dataInicio}" id="dataInicioAtividade"><f:convertDateTime   pattern="dd/MM/yyyy"  /></h:outputText>
															<c:if test="${not empty atividade.dataFim}">
																&nbsp; a &nbsp; 
															</c:if> 
														<h:outputText value="#{atividade.dataFim}" id="dataFimAtividade"><f:convertDateTime   pattern="dd/MM/yyyy"  /></h:outputText>						
													</td>
													<td style="text-align: center;">
														<h:outputText value="#{ atividade.cargaHoraria }" />
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</td>
							</tr>		
						</c:forEach>
					</td>
				</tr>


				<c:if test="${atividadeExtensao.obj.tipoAtividadeExtensao.id == PROGRAMA}">
					<tr>
						<td colspan="2">
							<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Ações Vinculadas ao <h:outputText value="#{atividadeExtensao.obj.tipoAtividadeExtensao.descricao}"/></h3>
							<t:dataTable value="#{atividadeExtensao.obj.atividades}" var="atividade" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" id="tbAtividades">
								<t:column>
									<f:facet name="header"><f:verbatim>Código - Título</f:verbatim></f:facet>
									<h:outputText value="#{atividade.codigoTitulo}" />
								</t:column>										
					
								<t:column width="10%">
									<f:facet name="header"><f:verbatim>Tipo</f:verbatim></f:facet>
									<h:outputText value="#{atividade.tipoAtividadeExtensao.descricao}" />
								</t:column>										
							</t:dataTable>
							<c:if test="${empty atividadeExtensao.obj.atividades}">
								<center><font color="red">Não há ações vinculadas</font></center>
							</c:if>
						</td>
					</tr>
				</c:if>	

				<tr>
					<td colspan="2">
									<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Ações das quais o <h:outputText value="#{atividadeExtensao.obj.tipoAtividadeExtensao.descricao}"/> faz parte</h3>
									<t:dataTable value="#{atividadeExtensao.obj.atividadesPai}" var="atividade" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" id="tbAtividadesPai">
										<t:column>
											<f:facet name="header"><f:verbatim>Código - Título</f:verbatim></f:facet>
											<h:outputText value="#{atividade.codigoTitulo}" />
										</t:column>										
					
										<t:column width="10%">
											<f:facet name="header"><f:verbatim>Tipo</f:verbatim></f:facet>
											<h:outputText value="#{atividade.tipoAtividadeExtensao.descricao}" />
										</t:column>										
								</t:dataTable>
								<c:if test="${empty atividadeExtensao.obj.atividadesPai}">
									<center><font color="red">Esta ação não faz parte de outros projetos ou programas de extensão</font></center>
								</c:if>
					</td>
				</tr>	


		<%-- 	LISTAS EXPECIFICAS DE PROGRAMA , temporário enquanto os novos formulário não então em produção --%>
		  <c:if test="${atividadeExtensao.obj.tipoAtividadeExtensao.id == PROGRAMA}">
		   
		
				<!-- OBJETIVOS / RESULTADOS ESPERADOS -->
				<c:if test="${not empty atividadeExtensao.objetivos}">
							<tr>
								<td colspan="2" >
								<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Objetivos / Resultados Esperados</h3>
						
											<t:dataTable value="#{atividadeExtensao.objetivos}" var="objetivo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" id="tbObjetivos">
													<t:column>
														<f:facet name="header"><f:verbatim>Objetivos</f:verbatim></f:facet>
														<h:outputText value="#{objetivo.objetivo}" escape="false"/>
													</t:column>
													<t:column>
														<f:facet name="header"><f:verbatim>Quantitativos</f:verbatim></f:facet>
														<h:outputText value="#{objetivo.quantitativos}" escape="false"/>
													</t:column>
													<t:column>
														<f:facet name="header"><f:verbatim>Qualitativos</f:verbatim></f:facet>
														<h:outputText value="#{objetivo.qualitativos}" escape="false"/>
													</t:column>												
											</t:dataTable>
						
								</td>
							</tr>	
				</c:if>
			
			
			    <!-- CRONOGRAMA -->
				<c:if test="${not empty atividadeExtensao.objetivos}">
						<tr>
							<td colspan="2">
								<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Cronograma</h3>
									<table class="formulario" width="99%">
											<thead>
												<tr>
													<th width="70%">Descrição das ativadades desenvolvidas</th>
													<th>Período</th>
												</tr>
											</thead>
											
											<tbody>
												<c:forEach items="#{atividadeExtensao.objetivos}" var="objet" varStatus="status1">
													<c:forEach items="#{objet.atividadesPrincipais}" var="ativ" varStatus="status2">					
														<tr class="${(status1.index + status2.index ) % 2 == 0 ? "linhaPar" : "linhaImpar" }">
															<td><h:outputText value="#{ativ.descricao}" escape="false"/></td>
															<td>
																<fmt:formatDate value="${ativ.dataInicio}" pattern="dd/MM/yyyy"/>
																<c:if test="${not empty ativ.dataFim}">
																	&nbsp; a &nbsp; 
																</c:if> 
																<fmt:formatDate value="${ativ.dataFim}" pattern="dd/MM/yyyy"/></td>
														</tr>
													</c:forEach>
												</c:forEach>
											</tbody>
	
	
									</table>
							</td>
						</tr>	
				</c:if>
					
			</c:if>

		
	  <%-- 	LISTAS EXPECIFICAS DE PROJETO (tipoAtividade = 1)	e PROGRAMA agora também --%>
	  <c:if test="${atividadeExtensao.obj.tipoAtividadeExtensao.id == PROJETO}">
	   
	
			<!-- OBJETIVOS / RESULTADOS ESPERADOS -->
			<c:if test="${not empty atividadeExtensao.objetivos}">
						<tr>
							<td colspan="2" >
							<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Objetivos / Resultados Esperados</h3>
					
										<t:dataTable value="#{atividadeExtensao.objetivos}" var="objetivo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" id="tbObjetivos">
												<t:column>
													<f:facet name="header"><f:verbatim>Objetivos</f:verbatim></f:facet>
													<h:outputText value="#{objetivo.objetivo}" escape="false"/>
												</t:column>
												<t:column>
													<f:facet name="header"><f:verbatim>Quantitativos</f:verbatim></f:facet>
													<h:outputText value="#{objetivo.quantitativos}" escape="false"/>
												</t:column>
												<t:column>
													<f:facet name="header"><f:verbatim>Qualitativos</f:verbatim></f:facet>
													<h:outputText value="#{objetivo.qualitativos}" escape="false"/>
												</t:column>												
										</t:dataTable>
					
							</td>
						</tr>	
			</c:if>
		
		
		    <!-- CRONOGRAMA -->
			<c:if test="${not empty atividadeExtensao.objetivos}">
					<tr>
						<td colspan="2">
							<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Cronograma</h3>
								<table class="formulario" width="99%">
										<thead>
											<tr>
												<th width="70%">Descrição das ativadades desenvolvidas</th>
												<th>Período</th>
											</tr>
										</thead>
										
										<tbody>
											<c:forEach items="#{atividadeExtensao.objetivos}" var="objet" varStatus="status1">
												<c:forEach items="#{objet.atividadesPrincipais}" var="ativ" varStatus="status2">					
													<tr class="${(status1.index + status2.index ) % 2 == 0 ? "linhaPar" : "linhaImpar" }">
														<td><h:outputText value="#{ativ.descricao}" escape="false"/></td>
														<td>
															<fmt:formatDate value="${ativ.dataInicio}" pattern="dd/MM/yyyy"/>
															<c:if test="${not empty ativ.dataFim}">
																&nbsp; a &nbsp; 
															</c:if> 
															<fmt:formatDate value="${ativ.dataFim}" pattern="dd/MM/yyyy"/></td>
													</tr>
												</c:forEach>
											</c:forEach>
										</tbody>


								</table>
						</td>
					</tr>	
			</c:if>
				
		</c:if>
	
	
	
			<!-- ORÇAMENTO DETALHADO -->
				<c:if test="${not empty atividadeExtensao.obj.orcamentosDetalhados}">
					<tr>
						<td colspan="2">
									<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Orçamento Detalhado</h3>			
									<table class="listagem" width="99%">
										<thead>
										<tr>
											<th>Descrição</th>
											<th style="text-align: right"  width="15%">Valor Unitário </th>
											<th style="text-align: right"  width="10%">Quant.</th>
											<th style="text-align: right" width="15%">Valor Total </th>
										</tr>
										</thead>
		
										<tbody>
		
											<c:if test="${not empty atividadeExtensao.tabelaOrcamentaria}">
											
												<c:set value="${atividadeExtensao.tabelaOrcamentaria}" var="tabelaOrcamentaria" />
												<c:forEach items="#{tabelaOrcamentaria}" var="tabelaOrc">
														
														<tr  style="background: #EFF0FF; font-weight: bold; padding: 2px 0 2px 5px;">
															<td colspan="5">${ tabelaOrc.key.descricao }</td>
														</tr>
																<c:set value="#{tabelaOrc.value.orcamentos}" var="orcamentos" />
																<c:forEach items="#{orcamentos}" var="orcamento" varStatus="status">
																	<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
																		<td style="padding-left: 20px"> ${orcamento.discriminacao}</td>
																		<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${orcamento.valorUnitario}" type="currency" />  </td>
																		<td align="right">${orcamento.quantidade}</td>
																		<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${orcamento.valorTotal}" type="currency"/>  </td>
																	</tr>
																</c:forEach>
		
														<tr  style="background: #EEE; padding: 2px 0 2px 5px;">
															<td colspan="2">SUB-TOTAL (${ tabelaOrc.key.descricao})</td>
															<td  align="right">${ tabelaOrc.value.quantidadeTotal }</td>
															<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${ tabelaOrc.value.valorTotalRubrica }" type="currency"/></td>
														</tr>
		
														<tr>
															<td colspan="5">&nbsp;</td>
														</tr>
		
												</c:forEach>
											</c:if>
		
												<c:if test="${empty atividadeExtensao.obj.orcamentosDetalhados}">
													<tr><td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font> </td></tr>
												</c:if>
		
										</tbody>
								</table>
						
						</td>
					</tr>	
				</c:if>
	
	
			<!-- ORÇAMENTO CONSOLIDADO -->
				<c:if test="${not empty atividadeExtensao.obj.orcamentosConsolidados}">
					<tr>
						<td colspan="2">
								<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Consolidação do Orcamento Solicitado</h3>
	
									<t:dataTable id="dt" value="#{atividadeExtensao.obj.orcamentosConsolidados}" var="consolidacao"
									 align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" rowIndexVar="index" forceIdIndex="true">
												<t:column>
													<f:facet name="header"><f:verbatim>Descrição</f:verbatim></f:facet>
													<h:outputText value="#{consolidacao.elementoDespesa.descricao}" />
												</t:column>
	
												<t:column>
													<f:facet name="header"><f:verbatim>FAEx (Interno)</f:verbatim></f:facet>
													<h:outputText value="#{consolidacao.fundo}"><f:convertNumber pattern="R$ #,##0.00"/></h:outputText>
												</t:column>
	
												<t:column>
													<f:facet name="header"><f:verbatim>Funpec</f:verbatim></f:facet>
													<h:outputText value="#{consolidacao.fundacao}"><f:convertNumber pattern="R$ #,##0.00"/></h:outputText>
												</t:column>
	
												<t:column>
													<f:facet name="header"><f:verbatim>Outros (Externo)</f:verbatim></f:facet>
													<h:outputText value="#{consolidacao.outros}"><f:convertNumber pattern="R$ #,##0.00"/></h:outputText>
												</t:column>
	
												<t:column style="text-align: right">
													<f:facet name="header"><f:verbatim>Total Rubrica</f:verbatim></f:facet>
													<h:outputText value="#{consolidacao.totalConsolidado}"><f:convertNumber pattern="R$ #,##0.00"/></h:outputText>
												</t:column>
								</t:dataTable>
	
						</td>
					</tr>	
				</c:if>
		
		
				<c:if test="${not empty atividadeExtensao.obj.arquivos}">
					<tr>
						<td colspan="2">
							<div class="infoAltRem">
								<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Arquivo	    							    		
							</div>
						</td>		
					</tr>
					
					<tr>
						<td colspan="2">
							<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Arquivos</h3>
							<t:dataTable value="#{atividadeExtensao.obj.arquivos}" var="arquivo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" id="tbArquivo">
									<t:column>
										<f:facet name="header">
											<f:verbatim>Descrição Arquivo</f:verbatim></f:facet>
											<h:outputText value="#{arquivo.descricao}" />
									</t:column>			
									<t:column width="5%">
										<h:outputLink value="/sigaa/verProducao?idProducao=#{arquivo.idArquivo}&key=#{ sf:generateArquivoKey(arquivo.idArquivo) }" title="Visualizar Arquivo">
											<h:graphicImage url="/img/view.gif" /> 
										</h:outputLink>
									</t:column>
							</t:dataTable>
						</td>
					</tr>	
				</c:if>
			
			
			<c:if test="${not empty atividadeExtensao.obj.fotos}">
			<tr>
				<td colspan="2">
					<div class="infoAltRem">
						<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Foto	    							    		
					</div>
				</td>
			</tr>

			<tr>
				<td colspan="2">
					<input type="hidden" value="0" id="idFotoOriginal" name="idFotoOriginal"/>
					<input type="hidden" value="0" id="idFotoMini" name="idFotoMini"/>
					<input type="hidden" value="0" id="idFotoProjeto" name="idFotoProjeto"/>

					<t:dataTable id="dataTableFotos" value="#{atividadeExtensao.obj.fotos}" var="foto" align="center" 
								 width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">

						<t:column>
							<f:facet name="header"><f:verbatim>Foto</f:verbatim></f:facet>
								<f:verbatim >
									<div class="foto">							
										<h:graphicImage url="/verFoto?idFoto=#{foto.idFotoMini}&key=#{sf:generateArquivoKey(foto.idFotoMini)}" width="70" height="70"/>
									</div>
								</f:verbatim>
						</t:column>

						<t:column>
							<f:facet name="header"><f:verbatim>Descrição da Foto</f:verbatim></f:facet>
							<h:outputText value="#{foto.descricao}" />
						</t:column>


						<t:column width="5%">
							<h:outputLink value="/sigaa/verFoto?idFoto=#{foto.idFotoOriginal}&key=#{sf:generateArquivoKey(foto.idFotoOriginal)}" id="link_verfoto_original_" title="Visualizar Foto">
								<h:graphicImage url="/img/view.gif" />
							</h:outputLink>						
						</t:column>
				
					</t:dataTable>
				</td>
			</tr>
			</c:if>
			 

			<!-- ORÇAMENTO APROVADO  -->
			<c:if test="${atividadeExtensao.obj.situacaoProjeto.id eq APROVADO}">
				<c:if test="${not empty atividadeExtensao.obj.orcamentosConsolidados}">
					<tr>
						<td colspan="2">
						
								<a name="orcamento_aprovado"/>
								
								<h3 style="text-align: center; background: #AAAAAA; font-size: 12px">Orçamento Aprovado</h3>
	
									<t:dataTable id="dt_aprovado" value="#{atividadeExtensao.obj.orcamentosConsolidados}" var="consolidacao"
									 align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" rowIndexVar="index" forceIdIndex="true">
												<t:column>
													<f:facet name="header"><f:verbatim>Descrição</f:verbatim></f:facet>
													<h:outputText value="#{consolidacao.elementoDespesa.descricao}" />
												</t:column>
	
												<t:column>
													<f:facet name="header"><f:verbatim>FAEx (Interno)</f:verbatim></f:facet>
													<h:outputText value="#{consolidacao.fundoConcedido}"><f:convertNumber pattern="R$ #,##0.00"/></h:outputText>
												</t:column>
									</t:dataTable>
	
						</td>
					</tr>	
				</c:if>
			</c:if>


		<c:if test="${not empty atividadeExtensao.obj.autorizacoesDepartamentos}">
			<tr>
				<td colspan="2">
								<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Lista de departamentos envolvidos na autorização da proposta</h3>
								<t:dataTable value="#{atividadeExtensao.obj.autorizacoesDepartamentos}" var="autorizacao" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" id="tbAutorizacao">
										<t:column rendered="#{autorizacao.ativo}">
											<f:facet name="header"><f:verbatim>Autorização</f:verbatim></f:facet>
											<h:outputText value="#{autorizacao.unidade.nome}" />
										</t:column>										
										<t:column rendered="#{autorizacao.ativo}">
											<f:facet name="header"><f:verbatim>Data Análise</f:verbatim></f:facet>
												<h:outputText value="#{autorizacao.dataAutorizacao}" ><f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss"/></h:outputText>												
										</t:column>																				
										<t:column rendered="#{autorizacao.ativo}">
											<f:facet name="header"><f:verbatim>Autorizado</f:verbatim></f:facet>
												<h:outputText value="#{autorizacao.autorizado?'SIM': 'NÃO'}" />												
										</t:column>
								</t:dataTable>
				</td>
			</tr>	
		</c:if>
	
	</tbody>
