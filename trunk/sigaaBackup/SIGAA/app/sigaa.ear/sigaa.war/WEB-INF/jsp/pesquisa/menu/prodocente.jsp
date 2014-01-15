<%@ taglib uri="/tags/struts-html" prefix="html"  %>

    <ul>
		<li> Cadastros
			<ul>
				<li><ufrn:link action="/prodocente/producao/SubTipoArtistico/lista.jsf" value="SubTipo Artistico" aba="prodocente"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoEvento/lista.jsf" value="Tipo de Evento" aba="prodocente"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoInstancia/lista.jsf" value="Tipo de Instância" aba="prodocente"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoMembroColegiado/lista.jsf" value="Tipo de Membro de Colegiado" aba="prodocente"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoParticipacao/lista.jsf" value="Tipo de Participação" aba="prodocente"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoParticipacaoOrganizacaoEventos/lista.jsf" value="Tipo de Organização em Eventos" aba="prodocente"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoParticipacaoSociedade/lista.jsf" value="Tipo de Participação em Sociedade" aba="prodocente"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoPeriodico/lista.jsf" value="Tipo de Períodico" aba="prodocente"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoProducaoTecnologica/lista.jsf" value="Tipo de Produção Tecnológica" aba="prodocente"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoRegiao/lista.jsf" value="Tipo de Região" aba="prodocente"/></li>
			</ul>
		</li>
		<li> Relatórios
			<ul>
			<li> <h:commandLink action="#{todaProducao.exibirOpcoes}" value="Relatório de Toda Produtividade" onclick="setAba('prodocente')" /> </li>
			<li> <h:commandLink action="#{producao.verRelatorioCotas}" value="Relatório para Concessão de Cotas de Pesquisa"/> </li>
			<li> <h:commandLink action="#{producao.verRelatorioProgressao}" value="Relatório de Progressão (Antigo GED)"/> </li>
			<li> <h:commandLink action="#{prodQuantitativo.verFormulario}" value="Quantitativos de Produção Acadêmica" onclick="setAba('prodocente')"/> </li>
			<li> <h:commandLink action="#{quantPesquisa.verFormulario}" value="Quantitativos de Pesquisa" onclick="setAba('prodocente')"/> </li>
			<li> <h:commandLink action="#{relatorioAvaliacaoGrupoPesquisaMBean.iniciar}" value="Relatório para avaliação de Grupos de Pesquisa" onclick="setAba('prodocente')">
					<f:param value="/prodocente/producao/relatorios/avaliacao_grupo_pesquisa/form.jsf" name="relatorio"/> 
				</h:commandLink>
			</li>
			<li> <h:commandLink value="Relatório dos Docentes com Baixa Produção" action="#{relatorioAvaliacaoGrupoPesquisaMBean.iniciar}" 
					onclick="setAba('prodocente')">
					<f:param value="/prodocente/producao/relatorios/avaliacao_grupo_pesquisa/form-geral.jsf" name="relatorio"/>
				</h:commandLink>
 			</li>
			</ul>
		</li>
		<li> Geração de Índices de Produtividade
			<ul>
				<li><h:commandLink action="#{classificacaoRelatorio.verificarPermissao}" value="Cálculo do Índice de Produção dos Docentes (Relatório-Espelho)" onclick="setAba('prodocente')"/></li>
				<li><h:commandLink action="#{classificacaoRelatorio.consultar}" value="Relatórios-Espelho Gerados" onclick="setAba('prodocente')"/></li>
			</ul>
		</li>
		<li> Bolsas de Produtividade
			<ul>
				<li><h:commandLink action="#{validacaoBolsaProdutividadeBean.popular}" value="Validar Bolsas Pendentes" onclick="setAba('prodocente')"/></li>
			</ul>
		</li>
	</ul>

    </ul>
