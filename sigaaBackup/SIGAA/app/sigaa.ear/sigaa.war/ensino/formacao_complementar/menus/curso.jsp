    <ul>
        <li>Curso
            <ul>
                <li><h:commandLink action="#{cursoTecnicoMBean.preCadastrar}" value="Cadastrar" onclick="setAba('curso')"/> </li>
                <li><h:commandLink action="#{cursoTecnicoMBean.listar}" value="Listar/Alterar" onclick="setAba('curso')"/> </li>
            </ul>
        </li>
		<li> Hor�rio
			<ul>
	        <li><h:commandLink action="#{horario.iniciar}" value="Cadastrar" onclick="setAba('curso')" /></li>
	        <li><h:commandLink action="#{horario.listar}" value="Listar/Alterar" onclick="setAba('curso')" /></li>
			</ul>
		</li>
        <li>Processos Seletivos
        	<ul>
                <li> <h:commandLink action="#{processoSeletivo.listar}" value="Gerenciar Processos Seletivos" onclick="setAba('curso')"/> </li>
                <li> <h:commandLink action="#{questionarioBean.gerenciarProcessosSeletivos}" value="Question�rios para Processos Seletivos" onclick="setAba('curso')"/> </li>
        	</ul>
        </li>
        <li>M�dulo
            <ul>
               	<li><h:commandLink action="#{moduloMBean.preCadastrar}" value="Cadastrar" onclick="setAba('curso')"/></li>
               	<li><h:commandLink action="#{moduloMBean.listar}" value="Listar/Remover" onclick="setAba('curso')"/></li>
			</ul>
        </li>
        <li> Componentes Curriculares
		    <ul>
	    	<li> <h:commandLink value="Cadastrar" action="#{componenteCurricular.preCadastrar}" onclick="setAba('curso')"/> </li>
			<li> <h:commandLink value="Listar/Alterar" action="#{componenteCurricular.listar}" onclick="setAba('curso')"/> </li>
		    </ul>
	    </li>
        <li>Estrutura Curricular
            <ul>
	            <li><h:commandLink action="#{estruturaCurricularTecnicoMBean.preCadastrar}" value="Cadastrar" onclick="setAba('curso')"/></li>
	            <li><h:commandLink action="#{estruturaCurricularTecnicoMBean.listar}" value="Listar/Alterar" onclick="setAba('curso')"/></li>
			</ul>
        </li>
		<c:if test="${especializacaoTurma.utilizadoPelaGestora}">
	    	<li>Especializa��o de Turma de Entrada
	    		<ul>
	    			<li><h:commandLink action="#{especializacaoTurma.preCadastrar}" value="Cadastrar" onclick="setAba('curso')"/></li>
	    			<li><h:commandLink action="#{especializacaoTurma.listar}" value="Alterar/Remover" onclick="setAba('curso')"/></li>
	    		</ul>
	    	</li>
    	</c:if>
    	<li>Turma de Entrada
    		<ul>
    			<li><h:commandLink action="#{turmaEntradaTecnicoMBean.preCadastrar}" value="Cadastrar" onclick="setAba('curso')"/></li>
    			<li><h:commandLink action="#{turmaEntradaTecnicoMBean.listar}" value="Listar/Alterar" onclick="setAba('curso')"/></li>
    		</ul>
    	</li>
    	<li>P�gina WEB
    		<ul>
    			<li><h:commandLink action="#{detalhesSite.listarCursosTecnico}" value="Gerenciar Portais P�blicos" onclick="setAba('curso')"/></li>
    		</ul>
    	</li>
    	<li> Opera��es Administrativas
    		<ul>
	    		<li><h:commandLink id="alterarStatusAluno" action="#{alteracaoStatusDiscente.iniciar}" value="Alterar Status de Aluno"  onclick="setAba('curso')"/> </li>
	    		<li><h:commandLink id="calendarioAcademico" action="#{calendario.iniciarFormacaoComplementar}" value="Calend�rio" onclick="setAba('curso')"/> </li>
	    		<li><h:commandLink id="parametrosGestoraAcademica" action="#{parametros.iniciarFormacaoComplementar}" value="Par�metros" onclick="setAba('curso')"/> </li>
	    		<li><h:commandLink id="retricoesaMatriculaOnLine" action="#{restricaoDiscenteMatriculaMBean.atualizar}" value="Restri��es Matr�cula On-Line" onclick="setAba('curso')"/> </li>
    		</ul>
    	</li>
    </ul>