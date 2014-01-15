<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>

	<a4j:keepAlive beanName="registroFrequenciaUsuariosBibliotecaMBean" />

<style>

	.header { 
		border:1px black solid;
		text-indent: 5px;  
		font-weight: bold;
		padding-left: 0px;
		padding-right: 0px;
		width: 6%;
		background: #DDD;
		text-align: right;
	}
	
	.headerTurnos { 
		font-weight: bold;
		padding-left: 0px;
		padding-right: 0px;
		width: 6%;
		text-align: right;
		border-bottom: 1px solid;
		border-left: 1px solid;
		border-top: 1px solid;
		border-right: 1px solid;
	}
	
	.headerTotal { 
		font-weight: bold;
		padding-left: 0px;
		padding-right: 0px;
		border-top: 1px solid;
		text-align: right;
		border-bottom: 1px solid;
		border-left: 1px solid;
		border-top: 1px solid;
		border-right: 1px solid;
	}
	
	.conteudo { 
		text-indent: 4px;  
		padding-left: 0px;
		padding-right: 0px;
		text-align: right;
		
		border-bottom: 1px solid;
		border-left: 1px solid;
		border-top: 1px solid;
		border-right: 1px solid;
	}
	
	.somatorio {
		text-align: center;
		border-top: 1px solid;
		text-align: right;
		border-bottom: 1px solid;
		border-left: 1px solid;
		border-top: 1px solid;
		border-right: 1px solid;		
	}
	
</style>

<div>
	<h3 align="center">Frequência de usuários</h3>

<br />

<div id="parametrosRelatorio">

	<table width="100%">
		<tr>
		<td width="12%"><b>Biblioteca:</b></td> 
			<td>
			<c:choose> 
				<c:when test="${registroFrequenciaUsuariosBibliotecaMBean.idBiblioteca == registroFrequenciaUsuariosBibliotecaMBean.TODAS_BIBLIOTECAS_SETORIAIS}">Todas as Setoriais</c:when>
				<c:when test="${registroFrequenciaUsuariosBibliotecaMBean.idBiblioteca == registroFrequenciaUsuariosBibliotecaMBean.TODAS_BIBLIOTECAS}">Todas</c:when>
			<c:otherwise> 
    			<h:outputText value="#{registroFrequenciaUsuariosBibliotecaMBean.biblioteca.descricao}"/>	 
  			</c:otherwise> 
			</c:choose>
			</td>
		</tr>
		<tr>
			<td><b>Ano:</b></td>
			<td>
				<h:outputText value="#{registroFrequenciaUsuariosBibliotecaMBean.ano}"/>	
			</td>
	</table>
</div>
	<br /><br />
	<table class="tabelaRelatorioBorda" align="center">
		
		<tr>
				<td class="headerTurnos" style="background: #DDD; border:1px black solid;">Turno</td>
				<td class="header" style="border:1px black solid;">Jan</td>
				<td class="header" style="border:1px black solid;">Fev</td>
				<td class="header" style="border:1px black solid;">Mar</td>			
				<td class="header" style="border:1px black solid;">Abr</td>			
				<td class="header" style="border:1px black solid;">Mai</td>			
				<td class="header" style="border:1px black solid;">Jun</td>			
				<td class="header" style="border:1px black solid;">Jul</td>			
				<td class="header" style="border:1px black solid;">Ago</td>			
				<td class="header" style="border:1px black solid;">Set</td>			
				<td class="header" style="border:1px black solid;">Out</td>			
				<td class="header" style="border:1px black solid;">Nov</td>			
				<td class="header" style="border:1px black solid;">Dez</td>
				<td class="headerTurnos" style="background: #DDD; border:1px black solid;">Total</td>
		</tr>

		<tr>
			<td class="headerTurnos">
				Matutino
			</td>
			
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesMatutino}" var="item">
				<c:if test="${item.mes == 1}">
						${item.totalAcessoJaneiro}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesMatutino}" var="item">
				<c:if test="${item.mes == 2}">
						${item.totalAcessoFevereiro}
				</c:if>
				</c:forEach>
			</td>
			
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesMatutino}" var="item">
				<c:if test="${item.mes == 3}">
						${item.totalAcessoMarco}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesMatutino}" var="item">
				<c:if test="${item.mes == 4}">
						${item.totalAcessoAbril}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesMatutino}" var="item">
				<c:if test="${item.mes == 5}">
						${item.totalAcessoMaio}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesMatutino}" var="item">
				<c:if test="${item.mes == 6}">
						${item.totalAcessoJunho}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesMatutino}" var="item">
				<c:if test="${item.mes == 7}">
						${item.totalAcessoJulho}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesMatutino}" var="item">
				<c:if test="${item.mes == 8}">
						${item.totalAcessoAgosto}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesMatutino}" var="item">
				<c:if test="${item.mes == 9}">
						${item.totalAcessoSetembro}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesMatutino}" var="item">
				<c:if test="${item.mes == 10}">
						${item.totalAcessoOutubro}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesMatutino}" var="item">
				<c:if test="${item.mes == 11}">
						${item.totalAcessoNovembro}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesMatutino}" var="item">
				<c:if test="${item.mes == 12}">
						${item.totalAcessoDezembro}
				</c:if>
				</c:forEach> 
			</td>
			
			<td class="somatorio">
				${registroFrequenciaUsuariosBibliotecaMBean.totalTodosMesesMatutino}
			</td>
		</tr>
		
		<!-- VESPERTINO -->
		
		<tr>
			<td class="headerTurnos">
				Vespertino
			</td>
			
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesVespertino}" var="item">
				<c:if test="${item.mes == 1}">
						${item.totalAcessoJaneiro}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesVespertino}" var="item">
				<c:if test="${item.mes == 2}">
						${item.totalAcessoFevereiro}
				</c:if>
				</c:forEach>
			</td>
			
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesVespertino}" var="item">
				<c:if test="${item.mes == 3}">
						${item.totalAcessoMarco}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesVespertino}" var="item">
				<c:if test="${item.mes == 4}">
						${item.totalAcessoAbril}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesVespertino}" var="item">
				<c:if test="${item.mes == 5}">
						${item.totalAcessoMaio}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesVespertino}" var="item">
				<c:if test="${item.mes == 6}">
						${item.totalAcessoJunho}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesVespertino}" var="item">
				<c:if test="${item.mes == 7}">
						${item.totalAcessoJulho}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesVespertino}" var="item">
				<c:if test="${item.mes == 8}">
						${item.totalAcessoAgosto}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesVespertino}" var="item">
				<c:if test="${item.mes == 9}">
						${item.totalAcessoSetembro}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesVespertino}" var="item">
				<c:if test="${item.mes == 10}">
						${item.totalAcessoOutubro}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesVespertino}" var="item">
				<c:if test="${item.mes == 11}">
						${item.totalAcessoNovembro}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesVespertino}" var="item">
				<c:if test="${item.mes == 12}">
						${item.totalAcessoDezembro}
				</c:if>
				</c:forEach> 
			</td>
			
			<td class="somatorio">
				${registroFrequenciaUsuariosBibliotecaMBean.totalTodosMesesVespertino}
			</td>
			
		</tr>
		
		<!-- NOTURNO -->
		
		<tr>
			<td class="headerTurnos">
				Noturno
			</td>
			
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesNoturno}" var="item">
				<c:if test="${item.mes == 1}">
						${item.totalAcessoJaneiro}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesNoturno}" var="item">
				<c:if test="${item.mes == 2}">
						${item.totalAcessoFevereiro}
				</c:if>
				</c:forEach>
			</td>
			
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesNoturno}" var="item">
				<c:if test="${item.mes == 3}">
						${item.totalAcessoMarco}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesNoturno}" var="item">
				<c:if test="${item.mes == 4}">
						${item.totalAcessoAbril}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesNoturno}" var="item">
				<c:if test="${item.mes == 5}">
						${item.totalAcessoMaio}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesNoturno}" var="item">
				<c:if test="${item.mes == 6}">
						${item.totalAcessoJunho}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesNoturno}" var="item">
				<c:if test="${item.mes == 7}">
						${item.totalAcessoJulho}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesNoturno}" var="item">
				<c:if test="${item.mes == 8}">
						${item.totalAcessoAgosto}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesNoturno}" var="item">
				<c:if test="${item.mes == 9}">
						${item.totalAcessoSetembro}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesNoturno}" var="item">
				<c:if test="${item.mes == 10}">
						${item.totalAcessoOutubro}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesNoturno}" var="item">
				<c:if test="${item.mes == 11}">
						${item.totalAcessoNovembro}
				</c:if>
				</c:forEach>
			</td>
			 
			<td class="conteudo">
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaMesesNoturno}" var="item">
				<c:if test="${item.mes == 12}">
						${item.totalAcessoDezembro}
				</c:if>
				</c:forEach> 
			</td>
			
			<td class="somatorio">
				${registroFrequenciaUsuariosBibliotecaMBean.totalTodosMesesNoturno}
			</td>
			
		</tr>
		
		<tr>
			
			<td class="headerTotal">
				Total
			</td>
			
			<td class="somatorio">
				${registroFrequenciaUsuariosBibliotecaMBean.somaJaneiroCol}
			</td>

			<td class="somatorio">
				${registroFrequenciaUsuariosBibliotecaMBean.somaFevereiroCol}
			</td>
			
			<td class="somatorio">
				${registroFrequenciaUsuariosBibliotecaMBean.somaMarcoCol}
			</td>

			<td class="somatorio">
				${registroFrequenciaUsuariosBibliotecaMBean.somaAbrilCol}
			</td>

			<td class="somatorio">
				${registroFrequenciaUsuariosBibliotecaMBean.somaMaioCol}
			</td>

			<td class="somatorio">
				${registroFrequenciaUsuariosBibliotecaMBean.somaJunhoCol}
			</td>
			
			<td class="somatorio">
				${registroFrequenciaUsuariosBibliotecaMBean.somaJulhoCol}
			</td>
			
			<td class="somatorio">
				${registroFrequenciaUsuariosBibliotecaMBean.somaAgostoCol}
			</td>
			
			<td class="somatorio">
				${registroFrequenciaUsuariosBibliotecaMBean.somaSetembroCol}
			</td>
			
			<td class="somatorio">
				${registroFrequenciaUsuariosBibliotecaMBean.somaOutubroCol}
			</td>
			
			<td class="somatorio">
				${registroFrequenciaUsuariosBibliotecaMBean.somaNovembroCol}
			</td>
			
			<td class="somatorio">
				${registroFrequenciaUsuariosBibliotecaMBean.somaDezembroCol}
			</td>
			
			<td class="conteudo">
				${registroFrequenciaUsuariosBibliotecaMBean.totalLinhasEColunas}
			</td>
			
		</tr>
		
	</table>
						
				<!--
				<c:forEach items="#{registroFrequenciaUsuariosBibliotecaMBean.listaFrequenciaUsuariosBib}" var="item">
						<tr>	
							<td> <fmt:formatDate pattern="dd/MM/yyyy" value="${item.dataCadastro}"/> </td>
							<td> ${ item.turno.descricao } </td>
							<td> ${ item.quantAcesso } </td>
						</tr>
				</c:forEach>
				-->
	<br/>
</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>