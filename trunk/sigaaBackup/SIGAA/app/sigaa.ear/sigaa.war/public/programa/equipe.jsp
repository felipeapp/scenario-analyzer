<%-- Cabeçalho --%>
<%@ include file="include/cabecalho.jsp" %>

<f:view locale="#{portalPublicoPrograma.lc}">
<a4j:keepAlive beanName="portalPublicoPrograma"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>

<%-- topo --%>
<%@ include file="include/programa.jsp" %>
<%-- menu flutuante --%>
<%@ include file="include/menu.jsp" %>
							
<%-- conteudo --%>
<div id="conteudo">
	<div class="titulo">
		<h:outputText value="#{idioma.corpoDocente}"/>
	</div>

	<c:set var="vinculo" value=""/>
	<c:set var="areac" value=""/>
	<c:set var="docentesProg" value="#{portalPublicoPrograma.docentesPrograma}"/>
	<c:choose>
	
		<c:when test="${not empty docentesProg}">
	
			<c:forEach items="${docentesProg}" var="equipeProgramaPos" varStatus="status">
						
				<%-- Início da Área de Concentração do Docente --%>
				<c:if test="${areac != equipeProgramaPos.areaConcentracaoPrincipal.denominacao}">
	
					<c:set var="vinculo" value=""/>
					<c:if test="${status.index!=0}">
								</tbody>
							</table>
						</div>
						<br clear="all"/>
					</c:if>
								
					<div id="listagem_tabela">
						<div id="group_lt">
								
							<c:choose>
								<c:when test="${not empty equipeProgramaPos.areaConcentracaoPrincipal.denominacao}">
									${equipeProgramaPos.areaConcentracaoPrincipal.denominacao}
								</c:when>
								<c:otherwise>
									<h:outputText value="#{idioma.semArea}" />
								</c:otherwise>
							</c:choose>
								
						</div>

						<table id="table_lt">
							<tbody>
							<tr class="campos">
								<td width="599"><h:outputText value="#{idioma.nome}" /></td>
								<td width="80"><h:outputText value="#{idioma.vinculo}" /></td>
								<td width="80"><h:outputText value="#{idioma.nivel}" /></td>
								<td width="80"><h:outputText value="#{idioma.telefone}" /></td>
								<td class="centro" width="46">C.Lattes</td>
								<td class="centro" width="35"><h:outputText value="#{idioma.email}" /></td>
							</tr>
				</c:if>
				<%-- Fim do Vinculo do Docente --%>

				<%-- Início Dados do Docente --%>
						<tr class="${status.index % 2 == 0 ? '' : 'linha_impar'}">
							<td  width="599">
							  <a class="cor" target="_blank" href="${ctx}/public/docente/portal.jsf?siape=${equipeProgramaPos.servidor.siape}"
							  	 target="_blank" title="${idioma.acessarPaginaPublicaDocente}">
								&nbsp;${equipeProgramaPos.nome}
							  </a> 
							</td>
							<td width="80">
								${equipeProgramaPos.vinculo.denominacao}
							</td>
							<td width="80">
								${equipeProgramaPos.nivel.denominacao}
							</td>
							<td width="80">
								${equipeProgramaPos.servidor.pessoa.telefone}
							</td>
							<td class="centro" width="46">
								<c:if test="${not empty equipeProgramaPos.servidor.perfil.enderecoLattes}">
									<a href="${ equipeProgramaPos.servidor.perfil.enderecoLattes}" 
										target="_blank" id="endereco-lattes" 
										title="${idioma.acessarCurriculoDe} ${idioma.professor}"> 
										<h:graphicImage url="/img/prodocente/lattes.gif" />
									</a> 
								</c:if>
								<c:if test="${not empty equipeProgramaPos.docenteExterno.perfil.enderecoLattes}">
									<a href="${ equipeProgramaPos.docenteExterno.perfil.enderecoLattes}" 
										target="_blank" id="endereco-lattes" 
										title="${idioma.acessarCurriculoDe} ${idioma.professor}"> 
										<h:graphicImage url="/img/prodocente/lattes.gif" />
									</a> 
								</c:if>
							</td>
							<td  class="centro" width="35">
							<c:if test="${not empty equipeProgramaPos.servidor.pessoa.email}">
							<a href="mailto:${equipeProgramaPos.servidor.pessoa.email}" title="${idioma.enviarEmailPara} ${idioma.professor}">
								<h:graphicImage url="../images/docente/icones/contato.png" />
							</a>
							</c:if>
							
							</td>
						</tr>
						<%--
						<c:set var="linhasPes" value="${equipeProgramaPos.linhasPesquisa}"/>
						
						
							<tr >
								<td colspan="5"   >
								<li style="text-indent:-3px;position:relative;display:list-item;margin-left:20px;"> 								
								<i> 	
									LINHAS DE PESQUISA:
									<c:choose>
										<c:when test="${not empty linhasPes}">
											<c:forEach items="${linhasPes}" var="linPesq" varStatus="status2">
												 ${linPesq.denominacao}
											<c:if test="${!status2.last}">,&nbsp;</c:if>
											</c:forEach>
										</c:when>
										<c:otherwise>
											NÃO INFORMADO
										</c:otherwise>	
									</c:choose>
								</i>
								</li>
								</td>
							</tr>
						 --%>	
						<%-- Fim Dados do Docente --%>

					  	<c:set var="vinculo" value="${equipeProgramaPos.nivel.denominacao}"/>  
						<c:set var="areac" value="${equipeProgramaPos.areaConcentracaoPrincipal.denominacao}"/>
					</c:forEach>
							</tbody>
					</table>
					
						</c:when>	
					<c:otherwise>
						<p class="vazio">
							<h:outputText value="#{idioma.vazio}"/>	
						</p>
					</c:otherwise>
				</c:choose>
				</div>	
			<%--  FIM CONTEÚDO  --%>	
			</div>
</f:view>
<%@ include file="./include/rodape.jsp" %>