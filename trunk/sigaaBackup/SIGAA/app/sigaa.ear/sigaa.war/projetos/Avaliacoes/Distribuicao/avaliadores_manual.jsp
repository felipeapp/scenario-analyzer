<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript" src="/shared/loadScript?src=javascript/jquery.tablesorter.min.js"></script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<f:view>
	<h2><ufrn:subSistema /> > Distribui��o Manual de Projetos para <h:outputText value="#{distribuicaoProjetoMbean.obj.tipoAvaliador.descricao}"/></h2>
	<br>

	<h:form id="form">
	  <table class="formulario" width="100%">
		<caption class="listagem">Distribuir Projeto Selecionado</caption>
	
			<tr>			
				<th width="25%" class="rotulo"> T�tulo: </th>
				<td><h:outputText value="#{distribuicaoProjetoMbean.projeto.titulo}" /></td>
			</tr>

			<tr>
				<th width="25%" class="rotulo"> Unidade Proponente: </th>
				<td><h:outputText value="#{distribuicaoProjetoMbean.projeto.unidade.nome}" /></td>
			</tr>

			<tr>
				<th width="25%" class="rotulo"> �rea do CNPq: </th>
				<td><h:outputText value="#{distribuicaoProjetoMbean.projeto.areaConhecimentoCnpq.nome}" /></td>
			</tr>


			<tr>
				<td colspan="2">
					<br/>
					<div class="infoAltRem">
					    <img src="${ ctx }/img/delete.gif" style="overflow: visible;" />: Remover Avaliador(a)   					    
					</div>
				</td>
			</tr>


			<tr>			
				<td colspan="2">
				  	<h:dataTable id="dtAvaliacoes"  value="#{distribuicaoProjetoMbean.projeto.avaliacoesAtivas}" 
				  		var="avaliacao" binding="#{distribuicaoProjetoMbean.avaliacoes}" 
				  		width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
	  						<f:facet name="caption">
								<h:outputText value="Lista das Avalia��es" />
							</f:facet>

							<t:column>
								<f:facet name="header">
									<f:verbatim>Avaliador(a)</f:verbatim>
								</f:facet>
								<h:outputText value="#{avaliacao.avaliador.pessoa.nome}" />
							</t:column>

							<t:column>
								<f:facet name="header">
									<f:verbatim>Tipo de Avalia��o</f:verbatim>
								</f:facet>
									<h:outputText value="#{avaliacao.distribuicao.modeloAvaliacao.tipoAvaliacao.descricao}" />		
									<h:outputText value=" (#{avaliacao.distribuicao.tipoAvaliador.descricao})" />
							</t:column>

							<t:column>
								<f:facet name="header">
									<f:verbatim>Nota</f:verbatim>
								</f:facet>
								<h:outputText value="#{avaliacao.nota}">
									<f:convertNumber pattern="#0.0"/>
								</h:outputText>
							</t:column>

							<t:column>
								<f:facet name="header">
									<f:verbatim>Situa��o</f:verbatim>
								</f:facet>
								<h:outputText value="#{avaliacao.situacao.descricao}" />
							</t:column>
							
							<t:column>
								<h:commandButton action="#{distribuicaoProjetoMbean.removerAvaliacao}" image="/img/delete.gif" value="Remover" id="btRemover" title="Remover Avaliador(a)"/>
							</t:column>
				  	</h:dataTable>
					<center><h:outputText  value="N�o h� avaliadores avaliando este projeto." rendered="#{empty distribuicaoProjetoMbean.projeto.avaliacoesAtivas}"/></center>
				</td>
			</tr>

			<tr>
				<td colspan="2">
					<br/>
					<div class="infoAltRem">
   					    <img src="${ ctx  }/img/adicionar.gif" style="overflow: visible;" />: Incluir Avaliador(a)
					</div>
				</td>
			</tr>

			<tr>			
				<td colspan="4">
						  	<h:dataTable id="dtAvaliadores"  value="#{distribuicaoProjetoMbean.avaliadoresDisponiveis}" 
						  		var="avaliador" binding="#{distribuicaoProjetoMbean.avaliadoresPossiveis }" 
						  		width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
			  						<f:facet name="caption">
										<h:outputText value="Lista de Poss�veis Avaliadores (#{distribuicaoProjetoMbean.obj.tipoAvaliador.descricao})" />
									</f:facet>
									
									<t:column>
										<f:facet name="header">
											<f:verbatim>Avaliador(a)</f:verbatim>
										</f:facet>
										<h:outputText value="#{avaliador.usuario.pessoa.nome}" />
									</t:column>

									<t:column>
										<f:facet name="header">
											<f:verbatim>�rea de Conhecimento</f:verbatim>
										</f:facet>
										<h:outputText value="#{avaliador.areaConhecimento1.nome}" />
									</t:column>

									<t:column>
										<f:facet name="header">
											<f:verbatim>Departamento/Unidade</f:verbatim>
										</f:facet>
										<h:outputText value="#{avaliador.usuario.servidor.lotacao}" />
									</t:column>

									<t:column>
										<h:commandButton action="#{distribuicaoProjetoMbean.adicionarAvaliacao}" image="/img/adicionar.gif" id="btAdicionar" title="Incluir Avaliador(a)" value="Incluir"/>
									</t:column>
						  	</h:dataTable>
						  	<center><h:outputText  value="N�o h� avaliadores dispon�veis para avaliar este projeto." rendered="#{empty distribuicaoProjetoMbean.avaliadoresDisponiveis}"/></center>
					</td>
				</tr>

			<tfoot>
				<tr>
					<td colspan="2" align="center">	
						<h:commandButton action="#{distribuicaoProjetoMbean.distribuir}" value="Confirmar Distribui��o" id="confirmarDistribuicao"/>
						<h:commandButton  action="#{distribuicaoProjetoMbean.distribuirNovoProjeto}" value="<< Distribuir Outro Projeto" id="distribuirOutroProjeto"/>						
						<h:commandButton  action="#{distribuicaoProjetoMbean.cancelar}" value="Cancelar"  id="cancelar" onclick="#{confirm}" immediate="true"/>						
					</td>		
				</tr>
			</tfoot>

    </table>
  </h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>