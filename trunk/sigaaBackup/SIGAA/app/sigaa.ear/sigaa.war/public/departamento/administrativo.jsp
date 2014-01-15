	<%@ include file="./include/cabecalho.jsp" %>
	<f:view  locale="#{portalPublicoDepartamento.lc}">
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
	<div id="colEsq">
		<%@ include file="./include/menu.jsp" %>
	</div>
	<div id="colDir">
		<%@ include file="./include/departamento.jsp" %>
		<div id="colDirCorpo">
		<!--  INÍCIO CONTEÚDO -->
		<div id="administrativo">
		<div id="titulo">
			<h1>Corpo Administrativo</h1>
		</div>
		<c:set var="_administrativos" value="#{portalPublicoDepartamento.corpoAdministrativo}"/>
		<c:set var="situacaoAnterior" value=""/>
		<c:set var="cargoAnterior" value=""/>
		<c:set var="contador" value="0"/>
		
		<c:if test="${not empty _administrativos}">
		
			<c:forEach var="adm" items="#{_administrativos}" varStatus="status">
					
				
				<table align="left" >
					<tr>
						<td class="foto">
							<c:choose>
								<c:when test="${not empty adm.idFoto && adm.idFoto>0}" >
									<img src="${ctx}/verFoto?idFoto=${adm.idFoto}&key=${ sf:generateArquivoKey(adm.idFoto) }" width="70" height="85"/>
								</c:when>
								<c:otherwise>
									<img src="${ctx}/img/no_picture.png" width="70" height="85"	/>
								</c:otherwise>
							</c:choose>&nbsp;
						</td>
						<td  class="descricao">
								<span class="nome">
									${adm.pessoa.nome} 	
									<c:if test="${not empty adm.perfil.telefone}">
									(${adm.perfil.telefone})
									</c:if>
								</span>
							<c:if test="${not empty adm.cargo.denominacao}">
								<span class="cargo">
									<i>${adm.cargo.denominacao}</i>
								</span>
							</c:if>
							<c:if test="${not empty adm.pessoa.email}">
								<br clear="all"/>
								<span class="email">
									<a href="#" title="Entrar em contato" id="${adm.pessoa.id}" onclick="arroba(this,'${fn:replace(adm.pessoa.email,'@','[at]')}')">
									E-mail
									</a>&nbsp;
								</span> 
							</c:if>		
									
						</td>
					</tr>
			</table>
			<c:if test="${contador % 2 != 0}">
				<br clear="all"/>
			</c:if>
			<c:set var="contador" value="${contador+1}"/>
			<c:set var="cargoAnterior" value="${adm.cargo.id}"/>
			<c:set var="situacaoAnterior" value="${adm.situacaoServidor.id}"/>
			</c:forEach>
			<br clear="all">
			
			</div>
			<table class="listagem" width="100%">
			<tfoot>
					<td colspan="5"><b>${fn:length(_administrativos)} Servidores(s) </b></td>
			</tfoot>
			</table>
			
		</c:if>
		<!--  FIM CONTEÚDO  -->	
	</div>
	</f:view>
	<%@ include file="../include/rodape.jsp" %>
	<script>
		function arroba(el,end){
			el.href= 'mailto:'+ end.replace("[at]","@");
		}	
	</script>	
	