<%@ taglib uri="/tags/struts-html" prefix="html"  %>

    <ul>
		<li> Cadastros
			<ul>
				<li><ufrn:link action="/prodocente/producao/SubTipoArtistico/lista.jsf" value="SubTipo Artistico" aba="prodocente"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoEvento/lista.jsf" value="Tipo de Evento" aba="prodocente"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoInstancia/lista.jsf" value="Tipo de Inst�ncia" aba="prodocente"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoMembroColegiado/lista.jsf" value="Tipo de Membro de Colegiado" aba="prodocente"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoParticipacao/lista.jsf" value="Tipo de Participa��o" aba="prodocente"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoParticipacaoOrganizacaoEventos/lista.jsf" value="Tipo de Organiza��o em Eventos" aba="prodocente"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoParticipacaoSociedade/lista.jsf" value="Tipo de Participa��o em Sociedade" aba="prodocente"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoPeriodico/lista.jsf" value="Tipo de Per�odico" aba="prodocente"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoProducaoTecnologica/lista.jsf" value="Tipo de Produ��o Tecnol�gica" aba="prodocente"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoRegiao/lista.jsf" value="Tipo de Regi�o" aba="prodocente"/></li>
			</ul>
		</li>
		<li> Relat�rios
			<ul>
			<li> <h:commandLink action="#{todaProducao.exibirOpcoes}" value="Relat�rio de Toda Produtividade" onclick="setAba('prodocente')" /> </li>
			<li> <h:commandLink action="#{producao.verRelatorioCotas}" value="Relat�rio para Concess�o de Cotas de Pesquisa"/> </li>
			<li> <h:commandLink action="#{producao.verRelatorioProgressao}" value="Relat�rio de Progress�o (Antigo GED)"/> </li>
			<li> <h:commandLink action="#{prodQuantitativo.verFormulario}" value="Quantitativos de Produ��o Acad�mica" onclick="setAba('prodocente')"/> </li>
			<li> <h:commandLink action="#{quantPesquisa.verFormulario}" value="Quantitativos de Pesquisa" onclick="setAba('prodocente')"/> </li>
			<li> <h:commandLink action="#{relatorioAvaliacaoGrupoPesquisaMBean.iniciar}" value="Relat�rio para avalia��o de Grupos de Pesquisa" onclick="setAba('prodocente')">
					<f:param value="/prodocente/producao/relatorios/avaliacao_grupo_pesquisa/form.jsf" name="relatorio"/> 
				</h:commandLink>
			</li>
			<li> <h:commandLink value="Relat�rio dos Docentes com Baixa Produ��o" action="#{relatorioAvaliacaoGrupoPesquisaMBean.iniciar}" 
					onclick="setAba('prodocente')">
					<f:param value="/prodocente/producao/relatorios/avaliacao_grupo_pesquisa/form-geral.jsf" name="relatorio"/>
				</h:commandLink>
 			</li>
			</ul>
		</li>
		<li> Gera��o de �ndices de Produtividade
			<ul>
				<li><h:commandLink action="#{classificacaoRelatorio.verificarPermissao}" value="C�lculo do �ndice de Produ��o dos Docentes (Relat�rio-Espelho)" onclick="setAba('prodocente')"/></li>
				<li><h:commandLink action="#{classificacaoRelatorio.consultar}" value="Relat�rios-Espelho Gerados" onclick="setAba('prodocente')"/></li>
			</ul>
		</li>
		<li> Bolsas de Produtividade
			<ul>
				<li><h:commandLink action="#{validacaoBolsaProdutividadeBean.popular}" value="Validar Bolsas Pendentes" onclick="setAba('prodocente')"/></li>
			</ul>
		</li>
	</ul>

    </ul>
