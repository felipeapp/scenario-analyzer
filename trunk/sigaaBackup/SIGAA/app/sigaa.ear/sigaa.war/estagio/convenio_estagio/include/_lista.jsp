<table class="listagem" style="width: 100%">
	<caption class="listagem">${nomeCaption} (${fn:length(convenios)})</caption>
	<thead>
		<tr>
			<th>Concedente</th>
			<th style="width: 15%;">Responsável pelo Convênio</th>
			<th style="width: 15%;">Tipo do Convênio</th>
			<th style="width: 15%;">Solicitado Por</th>
			<th style="width: 12%;">Data Solicitação</th>
			<th style="width: 5%;">Status</th>
			<th style="width: 3%;" colspan="3"></th>
		</tr>
	</thead>
	<c:forEach items="#{convenios}" var="convenio" varStatus="loop">	
		<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td>${convenio.concedente.pessoa.nome}</td>
			<td>${convenio.concedente.responsavel.pessoa.nome}</td>
			<td>${convenio.tipoConvenio.descricao}</td>
			<td>
				<a href="javascript://nop/" onClick="window.open('/shared/view_usuario.jsp?login=${convenio.registroCadastro.usuario.login}','','width=300,height=200, scrollbars');" title="${convenio.registroCadastro.usuario.login} - Ramal: ${convenio.registroCadastro.usuario.ramal}" >
					${convenio.registroCadastro.usuario.pessoa.nome}
				</a>
			</td>
			<td><ufrn:format type="dataHora" valor="${convenio.dataCadastro}"/></td>
			<td>${convenio.status.descricao}</td>
			<td>
				<h:commandLink action="#{convenioEstagioMBean.atualizar}" title="Alterar Convênio de Estágio"
					rendered="#{!convenioEstagioMBean.modoSeletor &&
						 (!convenioEstagioMBean.convenioEstagio && convenio.submetido && convenio.registroCadastro.usuario.id == convenioEstagioMBean.usuarioLogado.id
						  || convenioEstagioMBean.convenioEstagio)}">
					<h:graphicImage value="/img/alterar.gif"/>
					<f:param name="id" value="#{convenio.id}" />
				</h:commandLink>
			</td>	
			<td>
				<h:commandLink action="#{convenioEstagioMBean.view}" title="Visualizar Termo de Convênio de Estágio"
					rendered="#{!convenioEstagioMBean.modoSeletor && (convenioEstagioMBean.convenioEstagio ||
						!convenioEstagioMBean.convenioEstagio && convenio.idArquivoTermoConvenio != null)}" >
					<h:graphicImage value="/img/view.gif"/>
					<f:setPropertyActionListener value="#{convenio}" target="#{convenioEstagioMBean.obj}"/>
				</h:commandLink>
			</td>
			<td>
				<h:commandLink action="#{convenioEstagioMBean.selecionarConvenio}" title="Selecionar Convênio de Estágio" 
					rendered="#{ convenioEstagioMBean.modoSeletor }">
					<h:graphicImage value="/img/seta.gif"/>
					<f:setPropertyActionListener value="#{convenio}" target="#{convenioEstagioMBean.obj}"/>
				</h:commandLink>
				<h:commandLink action="#{convenioEstagioMBean.analisar}" title="Analisar Convênio de Estágio" 
					rendered="#{!convenioEstagioMBean.modoSeletor && convenioEstagioMBean.convenioEstagio && convenioEstagioMBean.permiteAnalisarConvenio}">
					<h:graphicImage value="/img/seta.gif"/>
					<f:setPropertyActionListener value="#{convenio}" target="#{convenioEstagioMBean.obj}"/>
				</h:commandLink>
			</td>
		</tr>					
	</c:forEach>
</table>	