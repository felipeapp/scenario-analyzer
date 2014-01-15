<%@page import="br.ufrn.comum.dominio.Sistema"%>
<div id="modulos">
	<h2 alt="O usu�rio tem acesso aos m�dulos que possu� *"> Menu Principal</h2>
	<ul>
		<li class="infantil ${acesso.infantilClass}"> <ufrn:link action="verMenuInfantil" disabled="${!acesso.infantil}"> 
				${acesso.infantil && acesso.acessibilidade ? '*Infantil e Fundamental' : 'Infantil e Fundamental'}  </ufrn:link> </li>
		<li class="medio ${acesso.medioClass}"> <ufrn:link action="verMenuMedio" disabled="${!acesso.medio}">
				${acesso.medio && acesso.acessibilidade ? '*M�dio' : 'M�dio'} </ufrn:link> </li>
		<li class="tecnico ${acesso.tecnicoClass}">
			<ufrn:link action="verMenuTecnico" disabled="${!acesso.tecnico && !acesso.coordenadorCursoTecnico}">
				${(acesso.tecnico || acesso.coordenadorCursoTecnico) && acesso.acessibilidade ?  '*T�cnico' : 'T�cnico'}</ufrn:link>
		</li>
		<li class="formacao_complementar ${acesso.formacaoComplementarClass}">
			<ufrn:link action="verMenuFormacaoComplementar" disabled="${!acesso.formacaoComplementar}">
				${(acesso.formacaoComplementar) && acesso.acessibilidade ?  '*Forma��o Complementar' : 'Forma��o Complementar'}</ufrn:link>
		</li>
		<li class="graduacao ${acesso.graduacaoClass}">
			<ufrn:link action="verMenuGraduacao" disabled="${!acesso.graduacao && !acesso.secretarioDepartamento}">
				${(acesso.graduacao || acesso.secretarioDepartamento) && acesso.acessibilidade ?  '*Gradua��o' : 'Gradua��o'}   </ufrn:link>
		</li>
		<li class="lato_sensu ${acesso.latoClass}">
			<ufrn:link action="verMenuLato" param="destino=lato" disabled="${!acesso.lato}"> 
				${acesso.lato && acesso.acessibilidade ? '*Lato Sensu' : 'Lato Sensu'} </ufrn:link>
		</li>
		<li class="stricto_sensu ${acesso.strictoClass}">
			<ufrn:link action="verMenuStricto" param="portal=ppg" disabled="${!acesso.stricto}">
				${acesso.ppg && acesso.acessibilidade ? '*Stricto Sensu' : 'Stricto Sensu'} </ufrn:link>
		</li>

		<li class="pesquisa ${acesso.pesquisaClass}">
			<ufrn:link action="verMenuPesquisa" disabled="${!acesso.pesquisa && !acesso.comissaoPesquisa}">
				${(acesso.pesquisa || acesso.comissaoPesquisa) && acesso.acessibilidade ? '*Pesquisa' : 'Pesquisa'}</ufrn:link>
		</li>
		<li class="extensao ${acesso.extensaoClass}">
			<ufrn:link action="verMenuExtensao" disabled="${!acesso.extensao}">
				${acesso.extensao && acesso.acessibilidade ? '*Extens�o' : 'Extens�o'}  </ufrn:link>
		</li>
		<li class="monitoria ${acesso.monitoriaClass}">
			<ufrn:link action="verMenuMonitoria" disabled="${!acesso.monitoria}">
				${acesso.monitoria && acesso.acessibilidade ? '*Monitoria' : 'Monitoria' } </ufrn:link>
		</li>
		<li class="acoes_associadas ${acesso.acoesAssociadasClass}">
			<ufrn:link action="verMenuAcoesAssociadas" disabled="${!acesso.acoesAssociadas}">
				${acesso.acoesAssociadas && acesso.acessibilidade ? '*A��es Acad�micas Integradas' : 'A��es Acad�micas Integradas'} </ufrn:link>
		</li>
		<li class="ead ${acesso.eadClass}"> 
			<ufrn:link action="verMenuSedis" disabled="${!acesso.ead}">
				${acesso.ead && acesso.acessibilidade ? '*Ensino a Dist�ncia' : 'Ensino a Dist�ncia'} </ufrn:link> 
		</li>
		<li class="sae ${acesso.saeClass}"> 
			<ufrn:link action="verMenuSae" disabled="${ !acesso.sae }">
				${acesso.sae && acesso.acessibilidade ? '*Assist�ncia ao Estudante' : 'Assist�ncia ao Estudante'} </ufrn:link> 
		</li>
		<li class="ouvidoria ${acesso.ouvidoriaClass}"> 
			<ufrn:link action="verMenuOuvidoria" disabled="${ !acesso.ouvidoria }">
				${acesso.ouvidoria && acesso.acessibilidade ? '*Ouvidoria' : 'Ouvidoria'} </ufrn:link> 
		</li>
		<li class="ambiente_virtual ${acesso.ambientesVirtuaisClass}"> 
			<ufrn:link action="verMenuAmbientesVirtuais" disabled="${ !acesso.ambientesVirtuais }">
				${acesso.ambientesVirtuais && acesso.acessibilidade ? '*Ambientes Virtuais' : 'Ambientes Virtuais'} </ufrn:link> 
		</li>
		<li class="prodocente ${acesso.prodocenteClass}">
			<ufrn:link action="verMenuProdocente" disabled="${!acesso.prodocente}">
				${acesso.prodocente && acesso.acessibilidade ? '*Produ��o Intelectual' : 'Produ��o Intelectual'}</ufrn:link> 
		</li>
		<li class="biblioteca ${acesso.bibliotecaClass}">
			<ufrn:link action="verMenuBiblioteca" disabled="${!acesso.moduloBiblioteca}">
				${acesso.moduloBiblioteca && acesso.acessibilidade ? '*Biblioteca' : 'Biblioteca'}</ufrn:link> 
		</li>

		<li class="diploma ${acesso.diplomaClass}">
			<ufrn:link action="verMenuRegistroDiplomas" disabled="${!acesso.moduloDiploma}">
				${acesso.moduloDiploma && acesso.acessibilidade ? '*Diplomas' : 'Diplomas'} </ufrn:link> 
		</li>
		
		<li class="convenio_estagio ${acesso.convenioEstagioClass}">
			<ufrn:link action="verMenuConveniosEstagio" disabled="${!acesso.moduloConvenioEstagio}">
				${acesso.moduloConvenioEstagio && acesso.acessibilidade ? '*Conv�nios de Est�gio' : 'Conv�nios de Est�gio'}</ufrn:link> 
		</li>		
		
		<li class="saude ${acesso.complexoHospitalarClass}">
			<ufrn:link action="verMenuComplexoHospitalar" disabled="${!acesso.complexoHospitalar}">
				${acesso.complexoHospitalar && acesso.acessibilidade ? '*Resid�ncias em Sa�de' : 'Resid�ncias em Sa�de'}</ufrn:link> 
		</li>
		<li class="vestibular ${ acesso.vestibularClass }"> 
			<ufrn:link action="verMenuVestibular" disabled="${!acesso.vestibular}">
				${acesso.vestibular && acesso.acessibilidade ? '*Vestibular' : 'Vestibular'} </ufrn:link> 
		</li>
		
		<li class="infra_fisica ${acesso.espacoFisicoClass}"> 
			<ufrn:link action="verMenuInfra" disabled="${ !acesso.espacoFisico }"> 
				${acesso.espacoFisico && acesso.acessibilidade ? '*Infraestrutura F�sica' : 'Infraestrutura F�sica'} </ufrn:link> 
		</li>
		
		<li class="nee ${acesso.neeClass}"> 
			<ufrn:link action="verMenuNee" disabled="${ !acesso.nee }"> 
				${acesso.nee && acesso.acessibilidade ? '*NEE' : 'NEE' } </ufrn:link> 
		</li>
		
		<li class="portal_avaliacao ${ acesso.avaliacaoClass }"> 
			<ufrn:link action="verPortalAvaliacao" disabled="${ !acesso.avaliacao }">
				${ acesso.avaliacao && acesso.acessibilidade ? '*Avalia��o Institucional' : 'Avalia��o Institucional'}</ufrn:link> 
		</li>

		<li class="administracao ${acesso.administracaoClass}">
			<ufrn:link action="verMenuAdministracao" disabled="${!acesso.administracao}">
				${acesso.administracao && acesso.acessibilidade ? '*Administra��o do Sistema' : 'Administra��o do Sistema'} </ufrn:link>
		</li>
		<li class="pap ${ acesso.papClass }">
			<ufrn:link action="verMenuProgramaAtualizacaoPedagogica" disabled="${!acesso.programaAtualizacaoPedagogica}">
				${acesso.programaAtualizacaoPedagogica && acesso.acessibilidade ? '*Prog. de Atual. Pedag�gica' : 'Prog. de Atual. Pedag�gica'}
			</ufrn:link> 
		 </li>
		 <li class="relacoes_internacionais ${ acesso.relacoesInternacionaisClass }">
			<ufrn:link action="verMenuRelacoesInternacionais" disabled="${!acesso.relacoesInternacionais}">
				${acesso.relacoesInternacionais && acesso.acessibilidade ? '*Rela��es Internacionais' : 'Rela��es Internacionais'}
			</ufrn:link> 
		 </li>
		
		 <br clear="all"/>
	</ul>
	
	<h2> Outros Sistemas </h2>
	<ul class="outros_sistemas">
		<li class="sipac ${acesso.sipacClass}">
			<ufrn:link action="entrarSistema" param="sistema=sipac" disabled="${!acesso.sipac}">
				Administrativo	
				(${acesso.sipac && acesso.acessibilidade ? '*SIPAC' : 'SIPAC'}) <br />
			</ufrn:link>
    	</li>
		<li class="sigrh ${acesso.sigrhClass}">
			<ufrn:link action="entrarSistema" param="sistema=sigrh" disabled="${!acesso.sigrh}">
				Recursos Humanos
				${acesso.sigrh && acesso.acessibilidade ? '*' : '' } (${configSistema['siglaSigrh'] }) <br />
			</ufrn:link>
		 </li>
		 
		 <% if (Sistema.isSigppAtivo()) { %>
		<li class="planejamento ${ acesso.sigpp ? 'on' : 'off' }">
			<ufrn:link action="entrarSistema" param="sistema=sigpp" disabled="${!acesso.sigpp}">
				 Planejamento (${ configSistema['siglaSigpp'] })
			</ufrn:link>
		</li>
		<% } %>
		 
		<li class="sigadmin ${ acesso.sipacClass }">
			<ufrn:link action="entrarSistema" param="sistema=sigadmin">
			${acesso.sipac && acesso.acessibilidade ? '*SIGAdmin' : 'SIGAdmin'} 
			</ufrn:link>
		 </li>
	</ul>
	
	<br clear="all"/>
</div>

<div id="portais">
	<h2> Portais </h2>
	<ul>
		<li class="docente ${acesso.docenteClass}"> <ufrn:link action="verPortalDocente" disabled="${!acesso.docente}">
			${acesso.docente && acesso.acessibilidade ? '*Portal do Docente' : 'Portal do Docente'}  </ufrn:link> </li>
		
		<li class="discente ${acesso.discenteClass}"> 
			<ufrn:link action="verPortalDiscente" disabled="${!acesso.discente ? !acesso.discenteMedio : false}" >
				${acesso.discente && acesso.acessibilidade ? '*Portal do Discente' : 'Portal do Discente'} 
			</ufrn:link> 
		</li>
		
		<li class="portal_lato ${acesso.coordenadorCursoLatoClass}"> <ufrn:link action="verMenuLato" param="destino=coordenacao"
			disabled="${!acesso.coordenadorCursoLato && !acesso.secretarioLato}"> 
			${(acesso.coordenadorCursoLato || acesso.secretarioLato) && acesso.acessibilidade ? '*Portal Coord. <br/> Lato Sensu' : 'Portal Coord. <br/> Lato Sensu'}  </ufrn:link></li>
	
		<li class="portal_stricto ${acesso.coordenadorCursoStrictoClass}"> <ufrn:link action="verMenuStricto"  param="portal=programa"
			disabled="${!acesso.coordenadorCursoStricto && !acesso.secretariaPosGraduacao}"> 
			${(acesso.coordenadorCursoStricto || acesso.secretariaPosGraduacao) && acesso.acessibilidade ? '*Portal Coord. <br/> Stricto Sensu' : 'Portal Coord. <br/> Stricto Sensu'}</ufrn:link></li>
	
		<li class="portal_graduacao ${acesso.coordenadorCursoGradClass}"> <ufrn:link action="verPortalCoordenadorGraduacao"
			disabled="${!acesso.coordenadorCursoGrad && !acesso.secretarioGraduacao && !acesso.coordenadorCursoGrad &&
				!acesso.coordenadorCursoStricto && !acesso.coordenacaoProbasica && !acesso.coordenadorEstagio}"> 
			${(acesso.coordenadorCursoGrad || acesso.secretarioGraduacao || acesso.coordenadorCursoGrad || acesso.coordenadorCursoStricto || 
			acesso.coordenacaoProbasica || acesso.coordenadorEstagio)  && acesso.acessibilidade ? '*Portal Coord. Gradua��o' : 'Portal Coord. Gradua��o'} </ufrn:link></li>

		<li class="tutor ${acesso.coordenadorPoloClass}"> <ufrn:link action="verPortalCoordPolo" disabled="${!acesso.coordenadorPolo}"> 
				${acesso.coordenadorPolo && acesso.acessibilidade ? '*Portal Coord. P�lo' : 'Portal Coord. P�lo'}  </ufrn:link> </li>
		<li class="tutor ${acesso.tutorClass}"> <ufrn:link action="verPortalTutor" disabled="${!acesso.tutorEad}"> 
				${acesso.tutorEad && acesso.acessibilidade ? '*Portal do Tutor' : 'Portal do Tutor'} </ufrn:link> </li> 
		<li class="portal_reitor ${acesso.cpdiClass}"> <ufrn:link action="verPortalCPDI" disabled="${!acesso.cpdi}">
				${acesso.cpdi && acesso.acessibilidade ? '*CPDI' : 'CPDI'} </ufrn:link></li>
		<li class="portal_reitor ${acesso.planejamentoClass}"> <ufrn:link action="verPortalPlanejamento" disabled="${!acesso.planejamento}">
				${acesso.planejamento && acesso.acessibilidade ? '*Portal da Reitoria' : 'Portal da Reitoria'} </ufrn:link> </li>
		<li class="portal_relatorios ${acesso.relatoriosClass}"> <ufrn:link action="verPortalRelatorios" disabled="${!acesso.relatorios}">
				${acesso.relatorios && acesso.acessibilidade ? '*Relat�rios de Gest�o' : 'Relat�rios de Gest�o'}</ufrn:link> </li>		
		<li class="portal_concedente_estagio ${acesso.portalConcedenteEstagioClass}"> <ufrn:link action="verPortalConcedenteEstagio" disabled="${!acesso.portalConcedenteEstagio}">
				${acesso.portalConcedenteEstagio && acesso.acessibilidade ? '*Portal do Concedente de Est�gio' : 'Portal do Concedente de Est�gio'}</ufrn:link> </li>
		<li class="portal_familiar ${acesso.portalFamiliarClass}"> <ufrn:link action="verPortalFamiliar" disabled="${!acesso.portalFamiliar}">
				${acesso.portalFamiliar && acesso.acessibilidade ? '*Portal do Familiar' : 'Portal do Familiar'}</ufrn:link> </li>				
	</ul>
</div>