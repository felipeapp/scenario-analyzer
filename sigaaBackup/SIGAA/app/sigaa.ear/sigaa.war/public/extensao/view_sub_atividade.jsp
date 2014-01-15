<%@include file="/public/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<jwr:style src="/public/css/extensao.css" media="all"/>
<f:view>
	<h:messages showDetail="true" />
	<h2>Visualização da Ação de Extensão</h2>
	<br>

<h:form id="form">	
	
	<%-- DADOS GERAIS, MINI CURSO --%>
	<table class="visualizacao">
			<caption> Mini-Curso </caption>
				
			<tr>
				<th> Título: </th>
				<td colspan="7"> <h:outputText value="#{consultaPublicaAtividadeExtensao.subAtividadeSelecionada.titulo}"/> </td>
			</tr>
			<tr>
				<th> Local: </th>
				<td colspan="7"> <h:outputText value="#{consultaPublicaAtividadeExtensao.subAtividadeSelecionada.local}"/> </td>
			</tr>	
			<tr>
				<th> Período: </th>
				<td>
					<h:outputText value="#{consultaPublicaAtividadeExtensao.subAtividadeSelecionada.inicio}"/> até
					<h:outputText value="#{consultaPublicaAtividadeExtensao.subAtividadeSelecionada.fim}"/>
				</td>
				
				<th> Horário:</th>
				<td>
					<h:outputText value="#{consultaPublicaAtividadeExtensao.subAtividadeSelecionada.horario}"/> 
				</td>
				
				<th> Carga Horária:</th>
				<td>	
					<h:outputText value="#{consultaPublicaAtividadeExtensao.subAtividadeSelecionada.cargaHoraria}"/>
				</td>
							
				<th>Número de Vagas:</th>
				<td>
					<h:outputText value="#{consultaPublicaAtividadeExtensao.subAtividadeSelecionada.numeroVagas}"/>
				</td>
			</tr>
					
	</table>
	<br />

	<br />
	<h4> Resumo </h4>
	<p> <h:outputText escape="false" value="#{consultaPublicaAtividadeExtensao.subAtividadeSelecionada.descricao}"/> </p>
	<br />

		
	<%-- 	LISTAS GERAIS, DE Ação 	--%>

	<!-- TIPOS DE PUBLICO ALVO -->
	<c:if test="${not empty consultaPublicaAtividadeExtensao.subAtividadeSelecionada.atividade.publicoAlvo}">
		<h4>Público Alvo </h4>
		<p>
			<h:outputText value="#{consultaPublicaAtividadeExtensao.subAtividadeSelecionada.atividade.publicoAlvo}"/>
		</p>
	<br />			
	</c:if>
	<!-- FIM TIPOS DE PUBLICO ALVO -->
		
		
		<br />
		<!-- MEBROS DA EQUIPE -->
		<c:if test="${not empty consultaPublicaAtividadeExtensao.subAtividadeSelecionada.atividade.membrosEquipe}">
			<h4>Membros da Equipe</h4>
			
			<c:forEach var="membro" items="#{consultaPublicaAtividadeExtensao.subAtividadeSelecionada.atividade.membrosEquipe}"  varStatus="status">
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

		<!-- FIM AÇÕES VINCULADAS -->


		<!-- ATIVIDADE QUE FAZ PARTE -->

		<h4>Atividade que faz parte</h4>
		<p>
		<h:outputText value="#{consultaPublicaAtividadeExtensao.subAtividadeSelecionada.atividade.titulo}"/>
		</p>
		<br />

		<!-- FIM AÇÕES QUE FAZ PARTE -->
		
				
		
		<br />
			<div style="margin: 0pt auto; width: 80%; text-align: center;">
				<a href="javascript:history.go(-1)">&lt;&lt; voltar</a>
			</div>
		<br />
		

</h:form>
</f:view>

<%@include file="/public/include/rodape.jsp" %>