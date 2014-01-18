<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
	
	<h2><ufrn:subSistema/> > Criar Turma</h2>
	<h:form>
	
			<div class="descricaoOperacao" style="width:90%">
				<b>Caro usuário,</b> 
				<br/><br/>
				 Nesta tela é possível escolher o componente curricular da turma a ser criada.
			</div>
				
			<div class="infoAltRem" style="width:90%">
				<img src="${ctx}/img/seta.gif"/>: Selecionar Componente
			</div>
				
			<table class="listagem" style="width:90%">
				<caption>${fn:length(turmaRedeMBean.componentes)} componentes encontrados</caption>
				<thead>
					<tr>
						<th style="text-align: left;">Componente Curricular</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
				<c:set var="disciplinaAtual" value="0" />
				<c:forEach items="#{turmaRedeMBean.componentes}" var="c" varStatus="loop">
					<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td id="nomeComp">${c.descricaoResumida}</td>
						<td>
						<h:commandLink action="#{turmaRedeMBean.submeterComponente}" title="Selecionar Componente" id="selecionarComponente">
							<h:graphicImage value="/img/seta.gif"></h:graphicImage>
							<f:param name="idComponente" value="#{c.id}" />
						</h:commandLink>
						</td>
					</tr>
				</c:forEach>
				</tbody>	
				<tfoot>
					<tr>
						<td colspan="2" align="center">
							<h:commandButton value="<< Selecionar Campus" action="#{turmaRedeMBean.telaSelecaoCampus}" id="voltarTelaCampus"/>
						</td>
					</tr>
				</tfoot>
			</table>
		
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
