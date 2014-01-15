<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2><ufrn:subSistema /> > Membros da Comiss�o</h2>
	<br>
	<h:form>
		<table align="center" class="formulario">
			<caption class="listagem"> Escolha a comiss�o e clique buscar </caption>
			<tr>
				<td>Comiss�o:</td>
				<td>
					<h:selectOneMenu id="comissao" value="#{membroComissao.comissaoBusca}">
						<f:selectItems value="#{membroComissao.tiposMembro}" />
					</h:selectOneMenu>
					&nbsp;
					<h:commandButton action="#{membroComissao.buscar}" value="Buscar" />

				</td>
			</tr>
		</table>
	</h:form>
	<br><br>

	<c:if test="${empty membroComissao.membros}">
		<center>Nenhum resultado foi encontrado.</center>
	</c:if>

	<c:if test="${not empty membroComissao.membros}">


		<div class="infoAltRem">
		    <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar membro da comiss�o
   		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover membro da comiss�o<br/>   		    
		</div>

	<h:form>
		<table class="listagem">
			<caption class="listagem">Membros da Comiss�o</caption>
			<thead>
			<tr>
				<th>Membro</th>
				<th>Final do Mandato</th>
				<th>Tipo de Comiss�o</th>								
				<th nowrap="nowrap"></th>								
			</tr>
			</thead>
			<tbody>
			<c:forEach items="#{membroComissao.membros}" var="membro">
				<tr>
					<td>${membro.servidor.siapeNome}</td>
					<td align="left">&nbsp;&nbsp;&nbsp;&nbsp;<ufrn:format type="data" valor="${membro.dataFimMandato}" /></td>
					<td>${membro.papelString}</td>
					<td width="2%" nowrap="nowrap">
							<h:commandLink  action="#{membroComissao.atualizar}" style="border: 0;" title="Alterar membro da comiss�o">
						    	<f:param name="id" value="#{membro.id}"/>
						      	<h:graphicImage url="/img/alterar.gif" />
							</h:commandLink>
							&nbsp;
							<h:commandLink  action="#{membroComissao.removeMembro}" style="border: 0;" title="Remover membro da comiss�o" onclick="return confirm('Aten��o! Deseja realmente remover este Membro da Comiss�o?');" >
						    	<f:param name="id" value="#{membro.id}"/>
						      	<h:graphicImage url="/img/delete.gif" />
							</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			</tbody>
			
		</table>
	</h:form>
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>