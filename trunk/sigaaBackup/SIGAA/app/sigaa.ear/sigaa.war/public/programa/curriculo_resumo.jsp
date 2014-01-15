<%-- CABEÇALHO --%>
<%@ include file="include/cabecalho.jsp" %>

<style>
	.colMaisMenos{width: 1%;font-weight: bold;}
	.colCodigo{width: 5% !important;text-align: center !important;}	
	.colComponente{width: 80% !important;}
	.colCargaHoraria{width: 10%;text-align: right !important;}
	.capitalize{text-transform: capitalize;}
	
	.verResumo {cursor: pointer !important;}
	.txtResumo {padding:8px 10px 8px; width:98%; text-align:justify;background-color: #F1F1F1;}
	.lupa {width:60px;padding-left:0px;}
	.ano {font-size:14px;}
	
</style>

<script>
	function toggleResumo(el,view){
		//Esconde todos elementos exceto ele mesmo
		jQuery(view + ':not(this)').slideUp();	
		jQuery('.colMaisMenos').html('[+]');
		if(el!= ""){
			//Altera a imagem background de todas os links
			jQuery(el).children('.colMaisMenos').html('[+]');
			if(jQuery(el).next(view).css("display") == "none"){			
				jQuery(el).next(view).slideDown("fast");
				jQuery(el).children('.colMaisMenos').html('[-]');
			}
		}	
	
	}
</script>

<f:view locale="#{portalPublicoPrograma.lc}">
<a4j:keepAlive beanName="portalPublicoPrograma"/>
<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>

<%-- TOPO --%>
<%@ include file="include/programa.jsp" %>
<%-- MENU FLUTUANTE --%>
<%@ include file="include/menu.jsp" %>
							
<%-- INÍCIO DO CONTEÚDO --%>
<div id="conteudo">

	<div class="titulo capitalize">
		${idioma.gradeCurricular}
		> ${fn:toLowerCase(curriculo.obj.curso.nivelDescricao)}
		> ${curriculo.obj.anoPeriodo}
	</div>
	
	<h:form id="formResumoCurriculoPosGraduacao">
	
		<c:set var="areaUltima" value=""/>		
		
		<div id="listagem_tabela">
		
		<c:choose>
		
			<c:when test="${not empty curriculo.obj.curriculoComponentes}">
						
				<c:forEach items="#{curriculo.obj.curriculoComponentes}" var="cc" varStatus="status">
					
					<%-- INÍCIO DO CABEÇALHO --%>
					<c:if test="${areaUltima != cc.areaConcentracao.id }">
						
						<c:if test="${!status.first}">
							</tbody>
						</table>
						<br clear="all"/>
						</c:if>
				
						<div id="group_lt">
							<h:outputText rendered="#{not empty cc.areaConcentracaoDescricao}" value="#{cc.areaConcentracaoDescricao}"/>
							<h:outputText rendered="#{empty cc.areaConcentracaoDescricao}"  value="#{idioma.semArea}"/>
						</div>
												
						<table id="table_lt">
							<tbody>
								<tr class="campos">
									<td></td>
									<td class="colCodigo"> <h:outputText value="#{idioma.codigo}"/> </td>
									<td class="colComponente"> <h:outputText value="#{idioma.componente}"/> </td>
									<td class="colCargaHoraria"> <h:outputText value="#{idioma.cargaHoraria}"/> </td>
								</tr>
					</c:if>
					<%-- FIM DO CABEÇALHO --%>
					
					<c:set var="areaUltima" value="${cc.areaConcentracao.id}"/>
					
					<c:choose>
						
						<c:when test="${not empty cc.componente.id}">
									
							<c:choose>
								<c:when test="${not empty cc.componente.detalhes.ementa}">					
									<tr class="${status.index % 2 == 0 ? 'linhaImpar' : 'linhaPar'}"
										onClick="toggleResumo(this,'.txtResumo');" 		
										title="${idioma.visualizarDetalhesComponente}" class="verResumo">
								</c:when>
								<c:otherwise>
									<tr class="${status.index % 2 == 0 ? 'linhaImpar' : 'linhaPar'}">
								</c:otherwise>
							</c:choose>	
								
								<td class="${not empty cc.componente.detalhes.ementa ? 'colMaisMenos':''}"> 
									<c:if test="${not empty cc.componente.detalhes.ementa}">
									[+]
									</c:if>
								</td>
								<td class="colCodigo"> 
									${cc.componente.codigo} 
								</td>
								<td class="colComponente"> 
									${cc.componente.nome} 
								</td>
								<td class="colCargaHoraria"> ${cc.componente.chTotal}h </td>
							</tr>
							<tr  class="txtResumo ${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								<td  colspan="3"> 
									${cc.componente.detalhes.ementa}
								</td>
							</tr>
						</c:when>
						
						<c:otherwise>
							<tr class="${status.index % 2 == 0 ? 'linhaImpar' : 'linhaPar'}">
								<td colspan="4"> <h:outputText value="#{idioma.vazio}"/> </td>
							</tr>
						</c:otherwise>
						
					</c:choose>			
				
				</c:forEach>
				
				</tbody>
				
			</table>
			
			</c:when>	

			<c:otherwise>
				<p class="vazio"> <h:outputText value="#{idioma.vazio}"/> </p>
			</c:otherwise>
			
		</c:choose>

		</div>	
		
		<br clear="all"/>
		<center>
			 <a title="${idioma.gradeCurricular}" href="curriculo.jsf?${portalPublicoPrograma.parametroURL}">
			 << Voltar
			 </a>
		</center>
		
	</h:form>
</div>
<%--  FIM DO CONTEÚDO  --%>	
	<rich:jQuery selector=".txtResumo" query="hide()" timing="onload" />

</f:view>
<%@ include file="./include/rodape.jsp" %>