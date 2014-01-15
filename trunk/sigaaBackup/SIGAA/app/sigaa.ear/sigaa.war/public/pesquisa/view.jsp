<%@include file="/public/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<jwr:style src="/public/css/pesquisa.css" media="all"/>
<style>
	table.visualizacao tr th,td{background-color:#FFF !important;}
</style>
<f:view>
	<h:messages showDetail="true" />
	<h2>Visualização do Projeto de Pesquisa</h2>
	<br>

<h:form id="form">
	<%-- DADOS GERAIS, DE TODOS OS TIPOS DE AÇÂO --%>
	<table class="visualizacao">
			<caption> Projeto de Pesquisa </caption>
				
			<tr>
				<th> Código: </th>
				<td colspan="5">
				 <h:outputText value="#{consultaProjetos.obj.codigo}"/>
				</td>
			</tr>
			
			<tr>	
				<th> Título: </th>
				<td colspan="5"><h:outputText value="#{consultaProjetos.obj.titulo}"/></td>
			</tr>
			
			<tr>
				<th width="20%"> Tipo do Projeto: </th>
				<td width="30%"> ${consultaProjetos.obj.interno ? "INTERNO" : "EXTERNO"}
					<c:choose>
						<c:when test="${consultaProjetos.obj.numeroRenovacoes > 0}">
							(${consultaProjetos.obj.numeroRenovacoes}ª Renovação)
						</c:when>
						<c:otherwise>
							(Projeto Novo)
						</c:otherwise>
					</c:choose>
				 </td>
				<th width="25%"> <%--  Categoria do Projeto: --%></th>
				<td width="25%"> <%-- ${consultaProjetos.obj.categoria.denominacao} --%> </td>
			</tr>
			
			<tr>
				<th> Situação: </th>
				<td>
				 <h:outputText value="#{consultaProjetos.obj.situacaoProjeto.descricao	}"/>
				</td>
				<th> E-mail: </th>
				<td >
					${consultaProjetos.obj.email}
				</td>
			</tr>	
			
			<tr>
				<th> Centro:	</th>
				<td colspan="3"> <h:outputText value="#{consultaProjetos.obj.centro.nome}"/> </td>
			</tr>
			
			<c:choose>		
					<c:when test="${consultaProjetos.obj.interno}">
						<tr>
							<th> Coordenador: </th>
							<td colspan="3">
							 <h:outputText value="#{consultaProjetos.obj.coordenador.pessoa.nome}"/>
							</td>
						</tr>
						<tr>
							<th> Edital:	</th>
							<td colspan="3"> ${consultaProjetos.obj.edital.descricao} </td>
						</tr>
						<tr>
							<th> Cota:	</th>
							<td colspan="3"> ${consultaProjetos.obj.edital.cota} </td>
						</tr>
					</c:when>
					<c:otherwise>
						<tr>
							<th> Coordenador: </th>
							<td>
							 <h:outputText value="#{consultaProjetos.obj.coordenador.pessoa.nome}"/>
							</td>
							<th> Período do Projeto: </th>
							<td>
								<fmt:formatDate value="${consultaProjetos.obj.dataInicio}"pattern="dd/MM/yyyy" />
								a <fmt:formatDate value="${consultaProjetos.obj.dataFim}"pattern="dd/MM/yyyy" />
							</td>
						</tr>
					</c:otherwise>
			</c:choose>
			<tr>
				<th> Palavra-Chave:	</th>
				<td colspan="3"> ${consultaProjetos.obj.palavrasChave} </td>
			</tr>
			
			<tr>
				<td colspan="4" class="subFormulario"> 
					Área de Conhecimento, Grupo e Linha de Pesquisa
				</td>
			</tr>

			<tr>
				<th> Área de Conhecimento: </th>
				<td colspan="3"> ${consultaProjetos.obj.areaConhecimentoCnpq.nome} </td>
			</tr>
		
			<tr>
				<th> Grupo de Pesquisa: </th>
				<td colspan="3"> ${consultaProjetos.obj.linhaPesquisa.grupoPesquisa.nomeCompleto} </td>
			</tr>
		
			<tr>
				<th> Linha de Pesquisa: </th>
				<td colspan="3"> ${consultaProjetos.obj.linhaPesquisa.nome} </td>
			</tr>
			
	</table>
	<br />

	<c:if test="${not empty consultaProjetos.obj.descricao}"> 
			<br />
			<h4> Descrição </h4>
			<p> <h:outputText escape="false" value="#{consultaProjetos.obj.descricao}"/> </p>
			<br />
	</c:if>
	<br />
	
	<!-- MEBROS DA EQUIPE -->
	<c:if test="${not empty consultaProjetos.obj.membrosProjeto}">
		<h4>Membros da Equipe</h4>
		
		<c:forEach var="membro" items="#{consultaProjetos.obj.membrosProjeto}"  varStatus="status">
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
					</span>
					<br clear="all"/>
					Categoria: <h:outputText value="#{membro.categoriaMembro.descricao}" />
					<br clear="all"/>
					
					Tipo : ${membro.funcaoMembro.descricao} 
					
					<h:outputText value="<font color='#3655a9' style='font-weight:bold;'>" 
					rendered="#{membro.coordenador}"  escape="false"/>
					
					<h:outputText value="#{membro.funcaoMembro.descricao}" 
					rendered="#{not empty membro.pessoa}" />
					
					<h:outputText value="</font>" rendered="#{membro.coordenador}" 
					escape="false"/>
					
					<c:if test="${not empty membro.servidor.pessoa.email}">
					<br clear="all"/>
					<a href="mailto:<h:outputText value="#{membro.servidor.pessoa.email}"/>" 
						title="Enviar e-mail." class="email">
						Entre em contato
					</a>
					</c:if>
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
		
	<br />
		<div style="margin: 0pt auto; width: 80%; text-align: center;">
			<a href="javascript:history.go(-1)">&lt;&lt; voltar</a>
		</div>
	<br />
	
</h:form>
</f:view>

<%@include file="/public/include/rodape.jsp" %>