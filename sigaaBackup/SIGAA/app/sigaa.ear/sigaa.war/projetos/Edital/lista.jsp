<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> &gt; Edital</h2>

<h:form id="form">
<div class="infoAltRem" style="width: 100%">
	<h:graphicImage url="/img/adicionar.gif" dir="LTR"/>
	<h:commandLink title="Cadastrar Novo Edital" value="Cadastrar Novo Edital" action="#{editalMBean.preCadastrar}" style="border: 0;"/>
	<h:graphicImage value="/img/view.gif"  style="overflow: visible;"/>: Visualizar Arquivo 
	<img src="/shared/img/alterar.gif" style="overflow: visible;"/>: Alterar 
	<img src="/shared/img/delete.gif" style="overflow: visible;"/>: Remover 
</div>
<a4j:keepAlive beanName="editalMBean" />
<c:if test="${ not empty editalMBean.all }">
			<table class="listagem">
				<caption>Lista de Editais</caption>
				<thead>
					<tr>
						<th>Descrição</th>
						<th style="text-align: center;">Início</th>
						<th style="text-align: center;">Fim</th>
						<th style="text-align: center;">Ano/Semestre</th>
						<th></th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<c:forEach items="#{editalMBean.all}" var="item"
					varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${item.descricao}</td>
						<td align="center"><fmt:formatDate
							value="${item.inicioSubmissao}" pattern="dd/MM/yyyy" /></td>
						<td align="center"><fmt:formatDate
							value="${item.fimSubmissao}" pattern="dd/MM/yyyy" /></td>
						<td align="center">${item.ano}/${item.semestre}</td>
						
						<td width="2%">
                              <h:commandLink title="Visualizar Arquivo" action="#{editalMBean.viewArquivo}" style="border: 0;">
                                      <f:param name="id" value="#{item.id}"/>
                                      <h:graphicImage url="/img/view.gif" />
                              </h:commandLink>
                        </td>   
						<td width="2%">
							<h:commandLink action="#{ editalMBean.atualizar }">
								<f:verbatim>
									<img src="/shared/img/alterar.gif" alt="Alterar" title="Alterar" />
								</f:verbatim>
								<f:param name="id" value="#{ item.id }" />
							</h:commandLink>
						</td>
						<td width="2%">
						  <h:commandLink action="#{ editalMBean.inativar }"
								onclick="#{confirmDelete}">
								<f:verbatim>
									<img src="/shared/img/delete.gif" alt="Remover" title="Remover" />
								</f:verbatim>
								<f:param name="id" value="#{ item.id }" />
						  </h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
