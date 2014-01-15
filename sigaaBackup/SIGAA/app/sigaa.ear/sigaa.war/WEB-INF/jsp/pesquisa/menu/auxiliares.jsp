<%@ taglib uri="/tags/struts-html" prefix="html"  %>

  
    <ul>
		<li> Áreas de Conhecimento CNPQ
            <ul>
    			<li><html:link action="/pesquisa/cadastroAreaConhecimento?dispatch=list&page=0&aba=cadastros">Listar</html:link></li>
            </ul>
		</li>
		<li>Grupos de Pesquisa
        	<ul>
                <li><h:commandLink action="#{ propostaGrupoPesquisaMBean.iniciar }" value="Cadastrar" onclick="setAba('cadastros');"/> </li>
                <li><h:commandLink action="#{grupoPesquisa.listar}" value="Listar/Alterar" onclick="setAba('cadastros');"/> </li>
                <li> <h:commandLink action="#{autorizacaoGrupoPesquisaMBean.listarGruposPesquisaPendentes}" value="Autorizar Grupo de Pesquisa" onclick="setAba('cadastros');"/> </li>
            </ul>
		</li>
        <li>Linha de Pesquisa
            <ul>
            	<li> <h:commandLink action="#{linhaPesquisaMBean.preCadastrar}" value="Cadastrar" onclick="setAba('cadastros')"/> </li>
                <li><html:link action="/pesquisa/cadastroLinhaPesquisa?dispatch=list&page=0&aba=cadastros">Alterar/Remover</html:link></li>
            </ul>
		</li>
        <li> Itens de Avaliação
            <ul>
		        <li><html:link action="/pesquisa/cadastroItemAvaliacao?dispatch=edit&aba=cadastros">Cadastrar</html:link></li>
    			<li><html:link action="/pesquisa/cadastroItemAvaliacao?dispatch=list&aba=cadastros">Listar/Alterar</html:link></li>
            </ul>
		</li>
		<li>
			Instituições de Ensino
			<ul>
				<li><h:commandLink action="#{instituicoesEnsino.preCadastrar}" value="Cadastrar" onclick="setAba('cadastros');"/> </li>
				<li><h:commandLink action="#{instituicoesEnsino.listar}" value="Listar/Alterar" onclick="setAba('cadastros');"/> </li>
			</ul>
		</li>
		<li>
			Tipos de Bolsa
			<ul>
				<li><h:commandLink action="#{tipoBolsaPesquisa.preCadastrar}" value="Cadastrar" onclick="setAba('cadastros');"/> </li>
				<li><h:commandLink action="#{tipoBolsaPesquisa.listar}" value="Listar/Alterar" onclick="setAba('cadastros');"/> </li>
			</ul>
		</li>
		<li>
			Entidade Financiadora
			<ul>
				<li><h:commandLink action="#{entidadeFinanciadora.preCadastrar}" value="Cadastrar" onclick="setAba('cadastros');"/> </li>
				<li><h:commandLink action="#{entidadeFinanciadora.listar}" value="Listar/Alterar" onclick="setAba('cadastros');"/> </li>
			</ul>
		</li>
		<li>
			Categorias de Projetos de Pesquisa
			<ul>
				<li><h:commandLink action="#{categoriaProjetoPesquisaBean.preCadastrar}" value="Cadastrar" onclick="setAba('cadastros');"/> </li>
				<li><h:commandLink action="#{categoriaProjetoPesquisaBean.listar}" value="Listar/Alterar" onclick="setAba('cadastros');"/> </li>
			</ul>
		</li>
		<li>
			Institutos de Ciência e Tecnologia
			<ul>
				<li><h:commandLink action="#{institutoCienciaTecnologia.preCadastrar}" value="Cadastrar" onclick="setAba('cadastros');"/> </li>
				<li><h:commandLink action="#{institutoCienciaTecnologia.listar}" value="Listar/Alterar" onclick="setAba('cadastros');"/> </li>
			</ul>
		</li>
		<li> Status Cota
			<ul>
				<li> <h:commandLink action="#{ statusCotaPlanoTrabalhoMBean.preCadastrar }" value="Cadastrar" onclick="setAba('cadastros');"/> </li>
				<li> <h:commandLink action="#{ statusCotaPlanoTrabalhoMBean.listar }" value="Listar/Alterar" onclick="setAba('cadastros');"/> </li>
			</ul>
		</li>
		<li> Área Tecnológica
			<ul>
				<li> Cursos
					<ul> 
						<li> <h:commandLink action="#{ cursosTecnologicosMBean.preCadastrar }" value="Cadastrar" onclick="setAba('cadastros');"/> </li>
						<li> <h:commandLink action="#{ cursosTecnologicosMBean.listar }" value="Listar/Alterar" onclick="setAba('cadastros');"/> </li> 
					</ul>
				</li>
				<li> Áreas de Conhecimento
					<ul> 
						<li> <h:commandLink action="#{ areaConhecimentoTecnMBean.preCadastrar }" value="Cadastrar" onclick="setAba('cadastros');"/> </li>
						<li> <h:commandLink action="#{ areaConhecimentoTecnMBean.listar }" value="Listar/Alterar" onclick="setAba('cadastros');"/> </li> 
					</ul>
				</li>
			</ul>
		</li>
       	<li>Função Membro Equipe
            <ul>
                <li><h:commandLink action="#{funcaoMembroEquipe.iniciarCadastro}" value="Cadastrar" onclick="setAba('cadastros')"/></li>
		        <li><a href="${ctx}/extensao/FuncaoMembro/lista.jsf?aba=cadastros">Listar/Alterar</a> </li>
            </ul>
		</li>
    </ul>