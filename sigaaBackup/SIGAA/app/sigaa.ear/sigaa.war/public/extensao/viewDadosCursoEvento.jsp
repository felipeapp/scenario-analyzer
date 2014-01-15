<%@include file="/public/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<jwr:style src="/public/css/extensao.css" media="all"/>
<f:view>
	<h:messages showDetail="true" />
	<h2>Visualização da Ação de Extensão</h2>
	<br>


<a4j:keepAlive beanName="inscricaoParticipanteAtividadeMBean" />


<h:form id="form">
	<%-- DADOS GERAIS, DE TODOS OS TIPOS DE AÇÂO --%>
	<table class="visualizacao">
			<caption> Ação de Extensão </caption>
				
			<tr>
				<th> Título: </th>
				<td colspan="7"> <h:outputText value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.titulo}"/> </td>
			</tr>	
			<tr>
				<th> Ano: </th>
				<td>
					<h:outputText value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.ano}"/>
				</td>
				
				<th>Nº Bolsas Concedidas:</th>
				<td>
					<h:outputText value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.bolsasConcedidas}"/>
					<c:if test="${empty inscricaoParticipanteAtividadeMBean.atividadeSelecionada.bolsasConcedidas}">
					Não Informado
					</c:if>
				</td>
				
				<th>Nº Discentes Envolvidos:</th>
				<td>	
					<h:outputText value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.totalDiscentes}"/>
				</td>
							
				<th>Público Estimado:</th>
				<td>
					<h:outputText value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.publicoEstimado}"/>
				</td>
			</tr>
			
			<tr>
				<th>Área Principal:</th>
				<td colspan="3">
					<h:outputText value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.areaTematicaPrincipal.descricao}"/>
				</td>
				
				<th>Área do CNPq:</th>
				<td colspan="3">
					<h:outputText value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.areaConhecimentoCnpq.nome}"/>
				</td>
			</tr>	
			
			<tr>
				<th width="20%">Unidade Proponente:</th>
				<td colspan="3" width="30%">
					<h:outputText value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.unidade.nome}"/>
				</td>
				<td colspan="4" rowspan="4" valign="top" width="50%">
					<span class="label"> Unidades Envolvidas:</span><br/>
					<c:if test="${not empty inscricaoParticipanteAtividadeMBean.atividadeSelecionada.unidadesProponentes}">
						<c:forEach var="unidadeEnvolvida" 
						items="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.unidadesProponentes}" 
						varStatus="i">
							<c:if test="${!i.first}">
								,
							</c:if>
					 		<h:outputText value="#{unidadeEnvolvida.unidade.nome}"/>
							<f:verbatim> / </f:verbatim>
							<h:outputText value="#{unidadeEnvolvida.unidade.gestora.sigla}"/>
						</c:forEach>
				 	</c:if>	
				</td>
			</tr>
			
			<tr>
				<th>Tipo:</th>
				<td colspan="3">
					<h:outputText value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.tipoAtividadeExtensao.descricao}"/>
				</td>
			</tr>
		
			<tr>
				<th>Municípios de Realização:</th>
				<td colspan="3">
					<c:if test="${(empty inscricaoParticipanteAtividadeMBean.atividadeSelecionada.programaExtensao) and (empty inscricaoParticipanteAtividadeMBean.atividadeSelecionada.produtoExtensao)}"> 
						<c:if test="${not empty inscricaoParticipanteAtividadeMBean.atividadeSelecionada.locaisRealizacao}">
							<c:forEach var="locaisRealizacao" items="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.locaisRealizacao}"  varStatus="status">
								${locaisRealizacao.municipioString} <br/>
							</c:forEach>
						</c:if>
					</c:if>
				</td>
			</tr>
		
			<tr>
				<th>Espaços de Realização:</th>
				<td colspan="3">	
					<c:if test="${(empty inscricaoParticipanteAtividadeMBean.atividadeSelecionada.programaExtensao) and (empty inscricaoParticipanteAtividadeMBean.atividadeSelecionada.produtoExtensao)}"> 
						
						<c:if test="${not empty inscricaoParticipanteAtividadeMBean.atividadeSelecionada.locaisRealizacao}">
							<c:forEach var="locaisRealizacao" items="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.locaisRealizacao}"  varStatus="status">
								${locaisRealizacao.descricao} <br/>
							</c:forEach>
						</c:if>
					</c:if>	
				</td>
		
			</tr>
		
			<tr>
				<th>Fonte de Financiamento:</th>
				<td colspan="8">
					<h:outputText value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.fonteFinanciamentoString}"/>
				</td>
			</tr>	
		
			<c:if test="${(inscricaoParticipanteAtividadeMBean.atividadeSelecionada.tipoCurso) or (inscricaoParticipanteAtividadeMBean.atividadeSelecionada.tipoEvento)}"> 
				
					<!-- AÇÃO EH UM CURSO -->
					<c:if test="${inscricaoParticipanteAtividadeMBean.atividadeSelecionada.tipoCurso}"> 
					<tr>
					 	<th>Modalidade do Curso:</th>
					 	<td colspan="3">
							<h:outputText value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.cursoEventoExtensao.modalidadeEducacao.descricao}"/>
						</td>
					
					 	<th>Tipo do Curso:</th>
						<td colspan="3">
							<h:outputText value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.cursoEventoExtensao.tipoCursoEvento.descricao}"/>
						</td>
					</tr>			
					</c:if>
				
				<tr>	
					<th>Tipo do Evento:</th>
					<td style="padding-left: 10px;"  colspan="3">	
						<!-- AÇÃO EH UM EVENTO -->
						<c:if test="${inscricaoParticipanteAtividadeMBean.atividadeSelecionada.tipoEvento}"> 
							<h:outputText value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.cursoEventoExtensao.tipoCursoEvento.descricao}"/>
						</c:if>
				  	</td>
				  	
					<th>Carga Horária:</th>
					<td>
						<h:outputText value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.cursoEventoExtensao.cargaHoraria}"/>
					</td>
					
					<th>Quantidade de Vagas:</th>
					<td>	
						<h:outputText value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.cursoEventoExtensao.numeroVagas}"/>
					</td>

				</tr>
			</c:if>

				<tr>	
					<th>Url da Acão:</th>
					<td colspan="7"> ${ inscricaoParticipanteAtividadeMBean.urlAcesso } </td>
				</tr>			
		<!-- FIM CURSO -->
			
		<%-- 	DADOS ESPECIFICOS DE PRODUTO (tipoAtividade = 4)	--%>
		<c:if test="${not empty inscricaoParticipanteAtividadeMBean.atividadeSelecionada.produtoExtensao}"> 
			<tr>
			  <th>Tipo do Produto: </th>
			  <td colspan="3">
				<h:outputText value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.produtoExtensao.tipoProduto.descricao}"/>
	          </td>

	          <th>Tiragem: </th>
			  <td colspan="3">
			  	<h:outputText value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.produtoExtensao.tiragem}"/> exemplares 
			  </td>
			</tr>
		</c:if>
		<!-- FIM DADOS ESPECIFICOS PRODUTO -->
			
	</table>
	<br />
	

	<c:if test="${inscricaoParticipanteAtividadeMBean.atividadeSelecionada.tipoProjeto}"> 
	<br />
			<h4> Resumo </h4>
			<p> <h:outputText escape="false" value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.projeto.resumo}"/> </p>
			<br />
	</c:if>
		

		<%-- 	DADOS ESPECIFICOS DE CURSO/EVENTO	--%>
			<c:if test="${(inscricaoParticipanteAtividadeMBean.atividadeSelecionada.tipoCurso) or (inscricaoParticipanteAtividadeMBean.atividadeSelecionada.tipoEvento)}"> 
			<c:if test="${not empty inscricaoParticipanteAtividadeMBean.atividadeSelecionada.cursoEventoExtensao.resumo}">
				<h4> Resumo </h4>
				<p> <h:outputText escape="false" value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.cursoEventoExtensao.resumo}"/>	</p>
				<br />
			</c:if>
		
			<c:if test="${not empty inscricaoParticipanteAtividadeMBean.atividadeSelecionada.cursoEventoExtensao.programacao}">
				<h4> Programação </h4>
				<p><h:outputText escape="false" value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.cursoEventoExtensao.programacao}"/></p>
				<br />
			</c:if>	
		</c:if>
		<!-- FIM DADOS ESPECIFICOS DE CURSO/EVENTO -->


		<%-- 	DADOS ESPECIFICOS DE PROGRAMA (tipoAtividade = 3)	--%>
		<c:if test="${not empty inscricaoParticipanteAtividadeMBean.atividadeSelecionada.programaExtensao}"> 
			<h4> Resumo </h4>
			<p> <h:outputText escape="false" value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.resumo}"/> </p>
		<br />
		</c:if>
		<%-- 	LISTAS GERAIS, DE Ação 	--%>

		<!-- TIPOS DE PUBLICO ALVO -->
		<c:if test="${not empty inscricaoParticipanteAtividadeMBean.atividadeSelecionada.publicoAlvo}">
			<h4>Público Alvo </h4>
			<p>
				<h:outputText value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.publicoAlvo}"/>
			</p>
		<br />			
		</c:if>
		<!-- FIM TIPOS DE PUBLICO ALVO -->
		
		
		<br />
		<!-- MEBROS DA EQUIPE -->
		<c:if test="${not empty inscricaoParticipanteAtividadeMBean.atividadeSelecionada.membrosEquipe}">
			<h4>Membros da Equipe</h4>
			
			<c:forEach var="membro" items="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.membrosEquipe}"  varStatus="status">
			<table align="left"  id="tbEquipe" class="equipeProjeto" >

				<tr>
					<td class="foto">
						<c:if test="${membro.discente.idFoto != null}">
							<img src="${ctx}/verFoto?idFoto=${membro.discente.idFoto}
						&key=${ sf:generateArquivoKey(membro.discente.idFoto) }" 
						width="70" height="85"/>
						</c:if>
						<c:if test="${membro.discente.idFoto == null}">
							<img src="${ctx}/img/no_picture.png" width="70" height="85"	/>
						</c:if>&nbsp;
					</td>
					<td  class="descricao">
						<span class="nome">
						<c:choose>	
							<c:when test="${membro.categoriaDocente}">		
								<a href='${ctx}/public/docente/portal.jsf?siape=
								${membro.servidor.siape}' target='_blank' >
						 		<h:outputText value="#{membro.pessoa.nome}" />
								</a>
						 	</c:when>
						 	<c:otherwise>
								<h:outputText value="#{membro.pessoa.nome}" />						 	
						 	</c:otherwise>
						</c:choose>
						<br clear="all"/>
						Categoria: <h:outputText value="#{membro.categoriaMembro.descricao}" />
						<br clear="all"/>
						
						Função : <h:outputText value="<font color='#3655a9' style='font-weight:bold;'>" 
						rendered="#{membro.coordenador}"  escape="false"/>
						
						<h:outputText value="#{membro.funcaoMembro.descricao}" 
						rendered="#{not empty membro.pessoa}" />
						
						<h:outputText value="</font>" rendered="#{membro.coordenador}" 
						escape="false"/>
						
						<br clear="all"/>
						<a href="mailto:<h:outputText value="#{membro.servidor.pessoa.email}"/>" 
							title="Enviar e-mail para o docente." class="email">
							Entre em contato
						</a>
					</td>
				</tr>
			</table>
			
			<c:if test="${!status.first && (status.index+1)%3 == 0}">
				<br clear="all"/>
			</c:if>
			</c:forEach>
			<br clear="all" />
			<br>
		 </c:if>
		 <!-- FIM MEBROS DA EQUIPE -->



		<!-- AÇÕES VINCULADAS -->
		<c:if test="${not empty inscricaoParticipanteAtividadeMBean.atividadeSelecionada.atividades}">
			<h4>Ações Vinculadas</h4>
			<t:dataTable value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.atividades}" var="atividade" align="center"
			  styleClass="listagem" rowClasses="linhaPar, linhaImpar" id="tbAtividades">
				
				<t:column>
				  	<f:facet name="header"><f:verbatim>Ano/Título</f:verbatim></f:facet>
					<h:commandLink style="color: #3655a9;" value="#{atividade.anoTitulo}"  title="Visualizar Ação" action="#{inscricaoParticipanteAtividadeMBean.visualizarDadosCursoEvento}">
							    <f:param name="idAtividadeExtensaoSelecionada" value="#{atividade.id}"/>
					</h:commandLink>
				</t:column>										
			</t:dataTable>
		<br />
		</c:if>
		<!-- FIM AÇÕES VINCULADAS -->


		<!-- AÇÕES QUE FAZ PARTE -->
		<c:if test="${not empty inscricaoParticipanteAtividadeMBean.atividadeSelecionada.atividadesPai}">
			<h4>Ações das quais faz parte</h4>
			<t:dataTable value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.atividadesPai}" var="atividadePai" align="center" width="100%" styleClass="listagemPublic" rowClasses="linhaPar, linhaImpar" id="tbAtividadesPai">
				<t:column>
					<p>
					<h:commandLink style="color: #3655a9;" value="#{atividadePai.projeto.anoTitulo}"  title="Visualizar Ação" action="#{inscricaoParticipanteAtividadeMBean.visualizarDadosCursoEvento}">
							    <f:param name="idAtividadeExtensaoSelecionada" value="#{atividadePai.id}"/>
					</h:commandLink>
					</p>
				</t:column>										
		</t:dataTable>
		<br />
		</c:if>
		<!-- FIM AÇÕES QUE FAZ PARTE -->
		
		
		<!-- LISTA DE FOTOS -->
			<h4>Lista de Fotos</h4>
			<t:dataTable id="dtFotos" value="#{inscricaoParticipanteAtividadeMBean.atividadeSelecionada.fotos}" var="foto" align="center" 
				width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">		
				<t:column>
					<p>
						<h:outputLink value="/sigaa/verFoto?idFoto=#{foto.idFotoOriginal}&key=#{sf:generateArquivoKey(foto.idFotoOriginal)}" id="link_verfoto_original_" title="Click para ampliar">
							<h:graphicImage url="/verFoto?idFoto=#{foto.idFotoMini}&key=#{sf:generateArquivoKey(foto.idFotoMini)}" width="70" height="70" alt="[Foto não Encotrada!]"/>
						</h:outputLink>
					</p>					
				</t:column>		
				<t:column>
					<p>	<h:outputText value="#{foto.descricao}" />	</p>
				</t:column>		
			</t:dataTable>
			
			<c:if test="${empty inscricaoParticipanteAtividadeMBean.atividadeSelecionada.fotos}">
					<p>Não há fotos cadastradas para esta ação</p>
			</c:if>
		<!-- FIM DE LISTA DE FOTOS -->
			
			<br /><br />
			
			<c:if test="${sessionScope.participanteCursosEventosExtensaoLogado == null}">
				<center>
					<h:outputLink title="Inscrever-se" value="/sigaa/link/public/extensao/acessarAreaInscrito">
						<h2> Clique aqui para fazer a sua Inscrição </h2>
					</h:outputLink>
				</center>
			</c:if>
			
			<div style="margin: 0pt auto; width: 80%; text-align: center;">
				<a href="javascript:history.go(-1)">&lt;&lt; voltar</a>
			</div>
			
		<br />

</h:form>
</f:view>

<%@include file="/public/include/rodape.jsp" %>