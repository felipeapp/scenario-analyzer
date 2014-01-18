<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<style>
.td_table {
    height: 8px;
    margin: 1px 1px 1px 1px;
    color: #000000;
    width: 1px;
}

</style>
<f:view>
<a4j:keepAlive beanName="justificativaCIC"/>
<h:form id="form">
<h2> <ufrn:subSistema /> &gt  Validar Justificativas de Ausências </h2>
	<table class="formulario" style="width: 100%;" >
	  <caption>Critério de busca</caption>
 		<tbody>
 			<tr><td></td></tr>
			<tr>
				<th width="10%">Tipo :</th>
				<td>
					<h:selectOneMenu value="#{justificativaCIC.tipo}" style="width: 30%">
						<f:selectItems value="#{justificativaCIC.allCombos}" />
					</h:selectOneMenu>
				</td>				
			</tr>			
			<tr><td></td></tr>
		</tbody>
		 <tfoot>
		    <tr>
				<td colspan="2">
					<h:commandButton value="Buscar" action="#{justificativaCIC.buscar}" id="buscar" />
					<h:commandButton value="Cancelar" action="#{justificativaCIC.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		    </tr>
		</tfoot>
	</table>
	<br/>
	
	<c:if test="${not empty justificativaCIC.ausentes}">	
		<div class="infoAltRem">
			<img src="/shared/img/icones/download.png" style="overflow: visible;"/>: Visualizar Arquivo Anexo
		</div>	
		<table class="listagem" style="width: 100%;"> 
		<caption>Ausentes Encontrados (${fn:length(justificativaCIC.ausentes)})</caption> 
		<thead>
				<tr>
					<td>Cadastrado Por</td>
					<td style="text-align: center">Status</td>
					<td style="text-align: center">Tipo</td>
					<td style="text-align: center" >Justificativa</td>
					<td style="text-align: center">Arquivo</td>
					<td style="text-align: center">Visto</td>
				</tr>
		</thead>
		<c:forEach items="#{justificativaCIC.ausentes}" var="item" varStatus="status">
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td  align="center"> 
				<h:outputText value="#{item.cadastrado_por.nome}"></h:outputText>
			</td>
			<td>${item.status == 1? "Registrado" : "Visto"  }</td>
			<td>${item.tipo == 1 ? "Autor" : "Avaliador"}</td>		
			<td align="center"> 
				<h:outputText value="#{item.justificativa}"></h:outputText>
			</td>
			<td  align="center">
			<c:if test="${not empty item.idArquivo}">
						<a href="${ctx}/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }"
						target="_blank"> 
						<img src="/shared/img/icones/download.png" border="0" alt="Visualizar Arquivo Anexo" 
						title="Visualizar Arquivo Anexo" />	</a>
					</c:if> 
					<c:if test="${empty item.idArquivo}">
						<img src="/sigaa/img/download_indisponivel.png" border="0" alt="Nenhum arquivo foi enviado" 
						title="Nenhum arquivo foi enviado" />
			</c:if>
			</td>
			<td>
				<h:selectBooleanCheckbox value="#{item.visto}" disabled="#{item.visto == true}"/>
			</td>
		</tr>
		</c:forEach>
		
			<tfoot>
				<tr>
					<td align="center" colspan="6">
						<h:commandButton value="Validar" action="#{justificativaCIC.atualizar}"  />
						<h:commandButton value="Cancelar" action="#{justificativaCIC.cancelar}"  />
					</td>
				</tr>
			</tfoot>
		</table>
		
		</c:if>
		
	</h:form>
</f:view>	
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>