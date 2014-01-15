    <ul>
		 <li>Curso
            <ul>
                <c:if test="${ cursoLatoMBean.gestorPodeCadastrarProposta }">
                	<li><h:commandLink action="#{cursoLatoMBean.preCadastrar}" value="Cadastrar Proposta de Curso Lato Sensu" onclick="setAba('curso');"/></li>
                </c:if>
                <li><h:commandLink action="#{buscaCursoLatoMBean.listar}" value="Gerenciar Propostas de Curso" onclick="setAba('curso');"/></li>
				<li><ufrn:link action="/prodocente/atividades/Coordenacao/form.jsf?aba=curso" value="Coordenação Curso (anterior a 2007)"/> </li>
				<li><h:commandLink action="#{prorrogacaoLatoBean.iniciar}" value="Prorrogar Prazo de Curso" onclick="setAba('curso');"/></li>
				<li><h:commandLink action="#{aprovarPropostaLato.carregarPropostasSubmetidas}" value="Aprovar Proposta de Curso" onclick="setAba('curso');"/></li>
				<li><h:commandLink action="#{buscaCursoLatoMBean.listar}" value="Criar Curso a partir de um curso existente" onclick="setAba('curso');"/></li>
			</ul>
		</li>
		
		<li>Relatórios Finais
         	<ul>
				<li><h:commandLink action="#{relatorioFinalLato.buscar}" value="Gerenciar" onclick="setAba('curso');"/></li>
         	</ul>
         </li>
         
		<li>Componentes Curriculares
         	<ul>
         		<li> <h:commandLink value="Buscar/Alterar" action="#{componenteCurricular.listar}" onclick="setAba('curso')"/> </li>
         	</ul>
         </li>

		<li>Tipo de Curso
         	<ul>
				<li> <h:commandLink action="#{tipoCursoLatoMBean.preCadastrar}" value="Cadastrar" onclick="setAba('curso')" id="cadastrar"/> </li>
				<li> <h:commandLink action="#{tipoCursoLatoMBean.listar}" value="Listar/Alterar" onclick="setAba('curso')" id="alterar_remover"/> </li>
         	</ul>
         </li>

		<li>Tipos de Financiamento
         	<ul>
				<li> <h:commandLink action="#{financiamentoCursoLatoSensuMBean.preCadastrar}" value="Cadastrar" onclick="setAba('curso')" id="cadastrar_financiamento"/> </li>
				<li> <h:commandLink action="#{financiamentoCursoLatoSensuMBean.listar}" value="Listar/Alterar" onclick="setAba('curso')" id="alterar_remover_financiamento"/> </li>
         	</ul>
         </li>
         
    </ul>