<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema/> &gt; Selecione a Ades�o</h2>
<style>
.alinharCentro{ 
	text-align:center !important;

}
</style>
<h:form>
	<div class="infoAltRem">
	    <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Escolher Ades�o
	</div>

	<table class="listagem" width="60%">
		<caption class="listagem">Todas as Ades�es do Discente</caption>
		<thead>
			<tr>
				<th>Ano-Periodo</th>
				<th class="alinharCentro">Priorit�rio</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td colspan="3" class="subFormulario">
					${ adesaoCadastroUnico.discente.pessoa.nome }
				</td>
			</tr>
			<c:forEach items="#{adesaoCadastroUnico.listaAdesao}" var="adesao" varStatus="status">
			<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td width="15%" nowrap="nowrap">${ adesao.ano }.${ adesao.periodo }</td>
				<td class="alinharCentro"><h:outputText value="#{ adesao.prioritarioFormatado }" /></td>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_COORDENADOR }  %>">
					<td style="text-align: right;">
						<h:commandLink title="Escolher Ades�o" action="#{adesaoCadastroUnico.escolherAdesao}">
							<f:param name="id" value="#{adesao.id}" />
							<h:graphicImage url="/img/seta.gif"/>
						</h:commandLink>				
					</td>
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE_VISUALIZAR_CADASTRO_UNICO }  %>">
					<td style="text-align: right;">
						<h:commandLink title="Escolher Ades�o" action="#{adesaoCadastroUnico.visualizarQuestionario}">
							<f:param name="id" value="#{adesao.id}" />
							<h:graphicImage url="/img/seta.gif"/>
						</h:commandLink>				
					</td>
				</ufrn:checkRole>
				
			</tr>
			</c:forEach>
		</tbody>
	
		
	</table>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>